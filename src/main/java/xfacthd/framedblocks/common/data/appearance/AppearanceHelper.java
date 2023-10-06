package xfacthd.framedblocks.common.data.appearance;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelDataManager;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.cache.StateCache;
import xfacthd.framedblocks.api.model.data.FramedBlockData;
import xfacthd.framedblocks.api.predicate.contex.ConTexMode;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.ClientConfig;

import java.util.function.Predicate;

public final class AppearanceHelper
{
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
        if (!FMLEnvironment.dist.isClient() || !framedBlock.getBlockType().supportsConnectedTextures())
        {
            return AIR;
        }

        ConTexMode mode = ClientConfig.conTexMode;
        if (mode == ConTexMode.NONE || queryPos == null)
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
        if (framedBlock.getBlockType().isDoubleBlock())
        {
            if (actualQueryState != null && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
            {
                if (canDoubleBlockConnectFullEdgeTo(pos, queryPos, actualQueryState, side, edge))
                {
                    if (stateCache.canConnectFullEdge(side, edge))
                    {
                        return getCamo(level, pos, side, edge);
                    }
                    return AIR;
                }

                if (edge == null)
                {
                    return AIR;
                }

                BlockState componentState = be.getComponentBySkipPredicate(level, actualQueryState, edge);
                if (componentState != null)
                {
                    IFramedBlock componentBlock = ((IFramedBlock) componentState.getBlock());
                    return getAppearance(componentBlock, componentState, level, pos, side, actualQueryState, queryPos);
                }
            }
            return AIR;
        }

        ConTexMode typeMode = framedBlock.getBlockType().getMinimumConTexMode();
        if (canUseMode(mode, typeMode, ConTexMode.FULL_FACE) && stateCache.canConnectFullEdge(side, null))
        {
            if (isNotFramedOrCanConnectFullEdgeTo(pos, queryPos, actualQueryState, side, edge))
            {
                return getCamo(level, pos, state);
            }
            return AIR;
        }

        if (edge == null)
        {
            // If the edge is null here, then there is no point in checking further
            return AIR;
        }

        if (canUseMode(mode, typeMode, ConTexMode.FULL_EDGE))
        {
            Direction conEdge = findFirstSuitableDirectionFromOffset(pos, queryPos, side, testEdge ->
                    stateCache.canConnectFullEdge(side, testEdge)
            );
            if (conEdge != null)
            {
                return getCamo(level, pos, state);
            }
        }

        if (mode == ConTexMode.DETAILED && !queryPos.equals(pos))
        {
            Direction detEdge = findFirstSuitableDirectionFromOffset(pos, queryPos, side, testEdge ->
                    isSideHidden(level, pos, state, testEdge)
            );
            if (detEdge != null && stateCache.canConnectDetailed(side, detEdge))
            {
                return getCamo(level, pos, state);
            }
        }
        return AIR;
    }

    private static boolean canUseMode(ConTexMode cfgMode, ConTexMode typeMode, ConTexMode targetMode)
    {
        return cfgMode.atleast(targetMode) && targetMode.atleast(typeMode);
    }

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

    /*
     * Non-null, non-AIR => connectable block
     * Non-null, AIR => framed block without CT support
     * Null => Double framed block, can't determine connecting component, won't connect to other double blocks,
     *         or neighbor state is actually air, in which case full-face and full-edge camos need to be returned
     */
    private static BlockState findApplicableNeighbor(BlockGetter level, BlockPos queryPos, @Nullable BlockState queryState)
    {
        if (queryState != null && queryState.getBlock() instanceof IFramedBlock block)
        {
            return block.getBlockType().supportsConnectedTextures() ? queryState : AIR;
        }

        BlockState actualQueryState = level.getBlockState(queryPos);
        if (actualQueryState.getBlock() instanceof IFramedBlock block)
        {
            IBlockType type = block.getBlockType();
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

        return queryState != null && queryState.isAir() ? null : queryState;
    }

    private static boolean canDoubleBlockConnectFullEdgeTo(
            BlockPos pos, BlockPos queryPos, BlockState queryState, Direction side, @Nullable Direction edge
    )
    {
        if (!(queryState.getBlock() instanceof IFramedBlock block))
        {
            return true;
        }

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
        return block.getCache(queryState).canConnectFullEdge(side, edge);
    }

    private static boolean isNotFramedOrCanConnectFullEdgeTo(
            BlockPos pos, BlockPos queryPos, BlockState queryState, Direction side, @Nullable Direction edge
    )
    {
        if (queryState != null && queryState.getBlock() instanceof IFramedBlock block)
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
            return block.getCache(queryState).canConnectFullEdge(side, edge);
        }
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    private static boolean isSideHidden(BlockGetter level, BlockPos pos, BlockState state, Direction side)
    {
        ModelDataManager manager = level.getModelDataManager();
        if (manager == null)
        {
            return false;
        }

        ModelData data = manager.getAt(pos);
        if (data == null)
        {
            return false;
        }

        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (fbData != null)
        {
            return fbData.isSideHidden(side);
        }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            fbData = be.getModelData(data, state).get(FramedBlockData.PROPERTY);
            return fbData != null && fbData.isSideHidden(side);
        }
        return false;
    }

    private static BlockState getCamo(BlockGetter level, BlockPos pos, Direction side, @Nullable Direction edge)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getCamo(side, edge).getState();
        }
        return AIR;
    }

    private static BlockState getCamo(BlockGetter level, BlockPos pos, BlockState state)
    {
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.getCamo(state).getState();
        }
        return AIR;
    }



    private AppearanceHelper() { }
}
