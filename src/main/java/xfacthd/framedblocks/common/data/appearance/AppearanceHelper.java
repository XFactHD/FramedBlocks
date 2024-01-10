package xfacthd.framedblocks.common.data.appearance;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelDataManager;
import net.neoforged.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.config.ClientConfig;

import java.util.function.Predicate;

public final class AppearanceHelper
{
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
    )
    {
        return getAppearance(framedBlock, state, level, pos, side, queryState, queryPos, false);
    }

    private static BlockState getAppearance(
            IFramedBlock framedBlock,
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            Direction side,
            @Nullable BlockState queryState,
            @Nullable BlockPos queryPos,
            boolean recursive
    )
    {
        IBlockType type = framedBlock.getBlockType();
        if (!FMLEnvironment.dist.isClient() || !type.supportsConnectedTextures())
        {
            return AIR;
        }

        ConTexMode cfgMode = ClientConfig.conTexMode;
        if (cfgMode == ConTexMode.NONE || queryPos == null)
        {
            // If queryPos is null, we can't make sure the connection is possible
            return AIR;
        }

        BlockState actualQueryState = findApplicableNeighbor(level, queryPos, queryState);
        if (actualQueryState == AIR)
        {
            // Don't perform additional checks against framed blocks without CT support
            return AIR;
        }

        Direction edge = findFirstSuitableDirectionFromOffset(pos, queryPos, side, $ -> true);
        StateCache stateCache = framedBlock.getCache(state);
        if (type.isDoubleBlock())
        {
            if (recursive)
            {
                LOGGER.error(
                        "AppearanceHelper#getAppearance() trying to recurse multiple times, this is a bug. " +
                        "Please report this to FramedBlocks with the following stacktrace. " +
                        "Pos: {}, State: {}, Side: {}",
                        pos, state, side, new Throwable()
                );
                return AIR;
            }

            if (actualQueryState != null)
            {
                if (isNotFramedOrCanConnectFullEdgeTo(pos, queryPos, actualQueryState, side, edge))
                {
                    if (!stateCache.canConnectFullEdge(side, edge))
                    {
                        return AIR;
                    }

                    BlockState componentState = framedBlock.getComponentAtEdge(level, pos, state, side, edge);
                    if (componentState == null)
                    {
                        return AIR;
                    }

                    FramedBlockData modelData = getModelData(level, pos, componentState, false);
                    return modelData != null ? modelData.getCamoState() : AIR;
                }

                if (edge == null)
                {
                    return AIR;
                }

                BlockState componentState = framedBlock.getComponentBySkipPredicate(level, pos, state, actualQueryState, edge);
                if (componentState != null)
                {
                    IFramedBlock componentBlock = ((IFramedBlock) componentState.getBlock());
                    return getAppearance(componentBlock, componentState, level, pos, side, actualQueryState, queryPos, true);
                }
            }
            return AIR;
        }

        FramedBlockData modelData = getModelData(level, pos, state, true);
        if (modelData == null)
        {
            // If the model data is inaccessible then there's no camo, so there's no point in continuing
            return AIR;
        }

        ConTexMode typeMode = type.getMinimumConTexMode();
        if (canUseMode(cfgMode, typeMode, ConTexMode.FULL_FACE) && stateCache.canConnectFullEdge(side, null))
        {
            if (isNotFramedOrCanConnectFullEdgeTo(pos, queryPos, actualQueryState, side, edge))
            {
                return modelData.getCamoState();
            }
            return AIR;
        }

        if (edge == null)
        {
            // If the edge is null here, then there is no point in checking further
            return AIR;
        }

        if (canUseMode(cfgMode, typeMode, ConTexMode.FULL_EDGE))
        {
            Direction conEdge = findFirstSuitableDirectionFromOffset(pos, queryPos, side, testEdge ->
                    stateCache.canConnectFullEdge(side, testEdge)
            );
            if (conEdge != null)
            {
                return modelData.getCamoState();
            }
        }

        if (cfgMode == ConTexMode.DETAILED && !queryPos.equals(pos))
        {
            Direction detEdge = findFirstSuitableDirectionFromOffset(pos, queryPos, side, modelData::isSideHidden);
            if (detEdge != null && stateCache.canConnectDetailed(side, detEdge))
            {
                return modelData.getCamoState();
            }
        }
        return AIR;
    }

    private static boolean canUseMode(ConTexMode cfgMode, ConTexMode typeMode, ConTexMode targetMode)
    {
        return cfgMode.atleast(targetMode) && targetMode.atleast(typeMode);
    }

    /**
     * Determine the first direction from the difference between the two given positions which matches the given predicate
     */
    private static Direction findFirstSuitableDirectionFromOffset(
            BlockPos pos, BlockPos queryPos, Direction side, Predicate<Direction> pred
    )
    {
        if (pos.equals(queryPos))
        {
            return null;
        }

        int nx = queryPos.getX() - pos.getX();
        int ny = queryPos.getY() - pos.getY();
        int nz = queryPos.getZ() - pos.getZ();
        Direction conFace = Utils.dirByNormal(nx, ny, nz);
        if (conFace != null)
        {
            return pred.test(conFace) ? conFace : null;
        }
        if (!Utils.isX(side))
        {
            conFace = Utils.dirByNormal(nx, 0, 0);
            if (conFace != null && pred.test(conFace))
            {
                return conFace;
            }
        }
        if (!Utils.isY(side))
        {
            conFace = Utils.dirByNormal(0, ny, 0);
            if (conFace != null && pred.test(conFace))
            {
                return conFace;
            }
        }
        if (!Utils.isZ(side))
        {
            conFace = Utils.dirByNormal(0, 0, nz);
            if (conFace != null && pred.test(conFace))
            {
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
    private static BlockState findApplicableNeighbor(BlockGetter level, BlockPos queryPos, @Nullable BlockState queryState)
    {
        if (queryState != null && queryState.getBlock() instanceof IFramedBlock queryBlock)
        {
            IBlockType type = queryBlock.getBlockType();
            if (type.isDoubleBlock())
            {
                return null;
            }
            return type.supportsConnectedTextures() ? queryState : AIR;
        }
        else if (queryState == null)
        {
            BlockState actualQueryState = level.getBlockState(queryPos);
            if (actualQueryState.getBlock() instanceof IFramedBlock queryBlock)
            {
                IBlockType type = queryBlock.getBlockType();
                if (type.isDoubleBlock())
                {
                    return null;
                }
                else if (!type.supportsConnectedTextures())
                {
                    return AIR;
                }
                return actualQueryState;
            }
            queryState = actualQueryState;
        }
        return queryState.isAir() ? null : queryState;
    }

    /**
     * Check that the querying block is either not a framed block or can be connected to from the given edge
     * of the given side
     */
    private static boolean isNotFramedOrCanConnectFullEdgeTo(
            BlockPos pos, BlockPos queryPos, BlockState queryState, Direction side, @Nullable Direction edge
    )
    {
        if (queryState != null && queryState.getBlock() instanceof IFramedBlock queryBlock)
        {
            int nx = queryPos.getX() - pos.getX();
            int ny = queryPos.getY() - pos.getY();
            int nz = queryPos.getZ() - pos.getZ();
            if (side.getAxis().choose(nx, ny, nz) != 0)
            {
                // CT impl is trying to check connection occlusion => check opposite side
                side = side.getOpposite();
            }
            if (edge != null)
            {
                edge = edge.getOpposite();
            }
            return queryBlock.getCache(queryState).canConnectFullEdge(side, edge);
        }
        return true;
    }

    private static FramedBlockData getModelData(
            BlockGetter level, BlockPos pos, BlockState componentState, boolean mayBeSingle
    )
    {
        ModelDataManager manager = level.getModelDataManager();
        if (manager == null)
        {
            return null;
        }

        ModelData data = manager.getAt(pos);
        if (data == null)
        {
            return null;
        }

        if (mayBeSingle)
        {
            FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
            if (fbData != null)
            {
                return fbData;
            }
        }

        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof IFramedBlock block)
        {
            return block.unpackNestedModelData(data, state, componentState).get(FramedBlockData.PROPERTY);
        }
        return null;
    }



    private AppearanceHelper() { }
}
