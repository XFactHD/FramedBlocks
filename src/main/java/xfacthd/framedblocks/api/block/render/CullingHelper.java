package xfacthd.framedblocks.api.block.render;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.camo.CamoContent;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.ConfigView;

/**
 * Helpers for checking whether an {@link IFramedBlock}'s side is occluded by the neighboring block or it occludes
 * a neighboring non-framed block.
 * These helpers must not be used outside the server and client thread, otherwise the {@link FramedBlockEntity}
 * lookups will fail due to safeguards in vanilla code.
 */
public final class CullingHelper
{
    /**
     * Test whether the given {@link IFramedBlock} is occluded on the given side by the neighboring block
     * and their camos either match or the camo of the occluding block is solid
     *
     * @param level The level the block is in
     * @param pos The position of the block
     * @param state The state of the block
     * @param side The side being tested for occlusion
     * @return true if the given block is occluded on the given side
     */
    public static boolean isSideHidden(BlockGetter level, BlockPos pos, BlockState state, Direction side)
    {
        BlockPos adjPos = pos.relative(side);
        BlockState adjState = level.getBlockState(adjPos);

        boolean adjFramed = false;
        IFramedBlock adjBlock = null;
        if (adjState.getBlock() instanceof IFramedBlock block)
        {
            if (block.shouldPreventNeighborCulling(level, adjPos, adjState, pos, state))
            {
                return false;
            }
            adjFramed = true;
            adjBlock = block;
        }
        else if (adjState.isSolidRender(level, adjPos))
        {
            // Let the game handle culling against fully solid cubes automatically,
            // prevents xray issues with block tool modifications like farmland tilling
            return false;
        }

        IFramedBlock block = (IFramedBlock) state.getBlock();
        boolean fullFace = block.getCache(state).isFullFace(side);
        if (!adjFramed || fullFace || !ConfigView.Client.INSTANCE.detailedCullingEnabled())
        {
            if (fullFace && (!adjFramed || adjBlock.getCache(adjState).isFullFace(side.getOpposite())))
            {
                if (!(level.getBlockEntity(pos) instanceof FramedBlockEntity be))
                {
                    return false;
                }

                CamoContent<?> camoContent = be.getCamo(side).getContent();
                if (adjFramed)
                {
                    if (!(level.getBlockEntity(adjPos) instanceof FramedBlockEntity adjBe))
                    {
                        return false;
                    }
                    CamoContent<?> adjCamoContent = adjBe.getCamo(side.getOpposite()).getContent();
                    return camoContent.isOccludedBy(adjCamoContent, level, pos, adjPos);
                }
                return camoContent.isOccludedBy(adjState, level, pos, adjPos);
            }
            return false;
        }

        SideSkipPredicate pred = block.getBlockType().getSideSkipPredicate();
        BlockState adjTestState = adjBlock.runOcclusionTestAndGetLookupState(pred, level, pos, state, adjState, side);
        if (adjTestState != null)
        {
            if (!(level.getBlockEntity(adjPos) instanceof FramedBlockEntity adjBe))
            {
                return false;
            }

            CamoContent<?> adjCamoContent = adjBe.getCamo(adjTestState).getContent();
            if (!adjCamoContent.isEmpty() && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
            {
                CamoContent<?> camoContent = be.getCamo(state).getContent();
                return camoContent.isOccludedBy(adjCamoContent, level, pos, adjPos);
            }
            return false;
        }
        return false;
    }

    /**
     * Test whether the given {@link IFramedBlock} occludes the neighboring non-{@link IFramedBlock} on the given side
     * @param block The occluding block
     * @param level The level the blocks are in
     * @param pos The position of the occluding block
     * @param state The occluding block
     * @param adjState The block being occluded
     * @param side The side of the occluding block which is occluding the neighboring block
     * @return true if the given side of this block occludes the neighboring block
     */
    public static boolean hidesNeighborFace(
            IFramedBlock block, BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    )
    {
        BlockPos adjPos = pos.relative(side);
        if (block.shouldPreventNeighborCulling(level, pos, state, adjPos, adjState) || adjState.getBlock() instanceof IFramedBlock)
        {
            return false;
        }
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            if (((IFramedBlock) state.getBlock()).getCache(state).isFullFace(side))
            {
                CamoContent<?> camoContent = be.getCamo(side).getContent();
                return camoContent.occludes(adjState, level, pos, adjPos);
            }
            return be.isSolidSide(side);
        }
        return false;
    }



    private CullingHelper() { }
}
