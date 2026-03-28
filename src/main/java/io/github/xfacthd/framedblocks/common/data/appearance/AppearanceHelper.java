package io.github.xfacthd.framedblocks.common.data.appearance;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.block.IBlockType;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.cache.StateCache;
import io.github.xfacthd.framedblocks.api.model.data.AbstractFramedBlockData;
import io.github.xfacthd.framedblocks.api.model.data.FramedBlockData;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.config.ClientConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public final class AppearanceHelper {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

    public static BlockState getAppearance(
            IFramedBlock framedBlock,
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos
    ) {
        if (!Utils.CLIENT_DIST) {
            return AIR;
        }

        ConTexMode cfgMode = ClientConfig.VIEW.getConTexMode();
        if (cfgMode == ConTexMode.NONE) {
            return AIR;
        }

        return getAppearance(framedBlock, state, level, pos, side, queryState, queryPos, cfgMode, false);
    }

    private static BlockState getAppearance(
            IFramedBlock framedBlock,
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos,
            ConTexMode cfgMode,
            boolean recursive
    ) {
        IBlockType type = framedBlock.getBlockType();
        if (!type.supportsConnectedTextures()) {
            return AIR;
        }

        boolean doubleBlock = type.isDoubleBlock();
        StateCache stateCache = state.framedblocks$getCache();
        if (!doubleBlock && !stateCache.mayConnect(side)) {
            return AIR;
        }

        BlockState actualQueryState = findApplicableNeighbor(level, queryPos, queryState);
        if (actualQueryState == AIR) {
            // Don't perform additional checks against framed blocks without CT support
            return AIR;
        }

        if (doubleBlock) {
            if (recursive) {
                LOGGER.error(
                        "AppearanceHelper#getAppearance() trying to recurse multiple times, this is a bug. " +
                        "Please report this to FramedBlocks with the following stacktrace. " +
                        "Pos: {}, State: {}, Side: {}",
                        pos, state, side, new Throwable()
                );
                return AIR;
            }

            if (actualQueryState != null) {
                Direction edge = findPreferredEdge(pos, queryPos, side, true, stateCache);

                if (isNotFramedOrCanConnectFullEdgeTo(pos, queryPos, actualQueryState, side, edge)) {
                    if (!stateCache.canConnectFullEdge(side, edge)) {
                        return AIR;
                    }

                    BlockState componentState = framedBlock.getComponentAtEdge(level, pos, state, side, edge);
                    if (componentState == null) {
                        return AIR;
                    }

                    FramedBlockData modelData = getModelData(level, pos, componentState);
                    return modelData != null ? modelData.getCamoContent().getAppearanceState() : AIR;
                }

                if (edge == null) {
                    return AIR;
                }

                BlockState componentState = framedBlock.getComponentBySkipPredicate(level, pos, state, actualQueryState, edge);
                if (componentState == null && DirUtils.dirByNormal(pos, queryPos) == null) {
                    // If pos-to-queryPos is diagonal and the first component lookup failed, try again with the other axis covered by the diagonal
                    Direction.Axis perpAxis = DirUtils.getPerpendicularAxis(side.getAxis(), edge.getAxis());
                    int nx = queryPos.getX() - pos.getX();
                    int ny = queryPos.getY() - pos.getY();
                    int nz = queryPos.getZ() - pos.getZ();
                    Direction newEdge = DirUtils.dirByNormal(perpAxis.choose(nx, 0, 0), perpAxis.choose(0, ny, 0), perpAxis.choose(0, 0, nz));
                    if (newEdge != null) {
                        componentState = framedBlock.getComponentBySkipPredicate(level, pos, state, actualQueryState, newEdge);
                    }
                }
                if (componentState != null) {
                    IFramedBlock componentBlock = ((IFramedBlock) componentState.getBlock());
                    return getAppearance(componentBlock, componentState, level, pos, side, actualQueryState, queryPos, cfgMode, true);
                }
            }
            return AIR;
        }

        FramedBlockData modelData = getModelData(level, pos, state);
        if (modelData == null) {
            // If the model data is inaccessible then there's no camo, so there's no point in continuing
            return AIR;
        }

        ConTexMode typeMode = type.getMinimumConTexMode();
        if (canUseMode(cfgMode, typeMode, ConTexMode.FULL_FACE) && stateCache.canConnectFullEdge(side, null)) {
            Direction edge = findPreferredEdge(pos, queryPos, side, false, stateCache);
            if (isNotFramedOrCanConnectFullEdgeTo(pos, queryPos, actualQueryState, side, edge)) {
                return modelData.getCamoContent().getAppearanceState();
            }
            return AIR;
        }

        if (queryPos == null) {
            // If queryPos is null here, the specific connection edge cannot be determined
            return AIR;
        }

        if (canUseMode(cfgMode, typeMode, ConTexMode.FULL_EDGE)) {
            Direction conEdge = findFirstSuitableDirectionFromOffset(pos, queryPos, side, stateCache, StateCache::canConnectFullEdge);
            if (conEdge != null) {
                return modelData.getCamoContent().getAppearanceState();
            }
        }

        if (cfgMode == ConTexMode.DETAILED && !queryPos.equals(pos)) {
            Direction detEdge = findFirstSuitableDirectionFromOffset(pos, queryPos, side, modelData, (ctx, _, testEdge) ->
                    ctx.isSideHidden(testEdge)
            );
            if (detEdge != null && stateCache.canConnectDetailed(side, detEdge)) {
                return modelData.getCamoContent().getAppearanceState();
            }
        }
        return AIR;
    }

    private static boolean canUseMode(ConTexMode cfgMode, ConTexMode typeMode, ConTexMode targetMode) {
        return cfgMode.atleast(targetMode) && targetMode.atleast(typeMode);
    }

    /**
     * Determine the preferred edge from the difference between the two given positions. Fixes the edge case of diagonal
     * checks failing on double blocks due to an edge covering both parts being selected
     */
    private static @Nullable Direction findPreferredEdge(BlockPos pos, @Nullable BlockPos queryPos, Direction side, boolean doubleBlock, StateCache stateCache) {
        if (queryPos == null) {
            return null;
        }
        if (doubleBlock) {
            Direction edge = findFirstSuitableDirectionFromOffset(pos, queryPos, side, stateCache, StateCache::canConnectFullEdge);
            if (edge != null) {
                return edge;
            }
        }
        return findFirstSuitableDirectionFromOffset(pos, queryPos, side, null, (_, _, _) -> true);
    }

    /**
     * Determine the first direction from the difference between the two given positions which matches the given predicate
     */
    private static <T> @Nullable Direction findFirstSuitableDirectionFromOffset(BlockPos pos, BlockPos queryPos, Direction side, @Nullable T context, EdgePredicate<T> pred) {
        if (pos.equals(queryPos)) {
            return null;
        }

        int nx = queryPos.getX() - pos.getX();
        int ny = queryPos.getY() - pos.getY();
        int nz = queryPos.getZ() - pos.getZ();
        Direction conFace = DirUtils.dirByNormal(nx, ny, nz);
        if (conFace != null) {
            return pred.test(context, side, conFace) ? conFace : null;
        }
        return findFirstSuitableDirectionFromMultiCoordOffset(nx, ny, nz, side, context, pred);
    }

    private static <T> @Nullable Direction findFirstSuitableDirectionFromMultiCoordOffset(
            int nx, int ny, int nz, Direction side, @Nullable T context, EdgePredicate<T> pred
    ) {
        if (!DirUtils.isX(side)) {
            Direction conFace = DirUtils.dirByNormal(nx, 0, 0);
            if (conFace != null && pred.test(context, side, conFace)) {
                return conFace;
            }
        }
        if (!DirUtils.isY(side)) {
            Direction conFace = DirUtils.dirByNormal(0, ny, 0);
            if (conFace != null && pred.test(context, side, conFace)) {
                return conFace;
            }
        }
        if (!DirUtils.isZ(side)) {
            Direction conFace = DirUtils.dirByNormal(0, 0, nz);
            if (conFace != null && pred.test(context, side, conFace)) {
                return conFace;
            }
        }
        return null;
    }

    /**
     * Determine the actual query state depending on whether it's a framed block, its CT support if it is and whether
     * it's a double block
     * <ul>
     *     <li>Non-null, non-AIR => connectable block</li>
     *     <li>Non-null, AIR => framed block without CT support</li>
     *     <li>Null => Double framed block, can't determine connecting component, won't connect to other double blocks,
     *     or neighbor state is actually air, in which case full-face and full-edge camos need to be returned</li>
     * </ul>
     */
    private static @Nullable BlockState findApplicableNeighbor(BlockGetter level, @Nullable BlockPos queryPos, @Nullable BlockState queryState) {
        if (queryState == null) {
            if (queryPos == null) {
                return null;
            }
            queryState = level.getBlockState(queryPos);
        }
        if (queryState.getBlock() instanceof IFramedBlock queryBlock) {
            IBlockType type = queryBlock.getBlockType();
            if (type.isDoubleBlock()) {
                return null;
            }
            return type.supportsConnectedTextures() ? queryState : AIR;
        }
        return queryState.isAir() ? null : queryState;
    }

    /**
     * Check that the querying block is either not a framed block or can be connected to from the given edge
     * of the given side
     */
    private static boolean isNotFramedOrCanConnectFullEdgeTo(
            BlockPos pos, @Nullable BlockPos queryPos, @Nullable BlockState queryState, Direction side, @Nullable Direction edge
    ) {
        if (queryPos == null && queryState != null) {
            if (queryState.getBlock() instanceof IFramedBlock) {
                return queryState.framedblocks$getCache().canConnectFullEdge(side, null);
            }
            return true;
        }
        if (queryState != null && queryState.getBlock() instanceof IFramedBlock) {
            int nx = queryPos.getX() - pos.getX();
            int ny = queryPos.getY() - pos.getY();
            int nz = queryPos.getZ() - pos.getZ();
            if (side.getAxis().choose(nx, ny, nz) != 0) {
                // CT impl is trying to check connection occlusion => check opposite side
                side = side.getOpposite();
            }
            // Specially handle diagonal lookups to fix cases where an edge unsuitable for the query block is selected
            if ((nx != 0 || ny != 0 || nz != 0) && DirUtils.dirByNormal(nx, ny, nz) == null) {
                EdgePredicate<StateCache> predicate = (cache, testSide, testEdge) -> cache.canConnectFullEdge(testSide, testEdge.getOpposite());
                return findFirstSuitableDirectionFromMultiCoordOffset(nx, ny, nz, side, queryState.framedblocks$getCache(), predicate) != null;
            }
            if (edge != null) {
                edge = edge.getOpposite();
            }
            return queryState.framedblocks$getCache().canConnectFullEdge(side, edge);
        }
        return true;
    }

    private static @Nullable FramedBlockData getModelData(BlockGetter level, BlockPos pos, BlockState componentState) {
        ModelData data = level.getModelData(pos);
        if (data == ModelData.EMPTY) {
            return null;
        }

        AbstractFramedBlockData fbData = data.get(AbstractFramedBlockData.PROPERTY);
        return fbData != null ? fbData.unwrap(componentState) : null;
    }

    @FunctionalInterface
    private interface EdgePredicate<T> {
        boolean test(@UnknownNullability T context, Direction side, Direction edge);
    }

    private AppearanceHelper() { }
}
