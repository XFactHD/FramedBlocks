package xfacthd.framedblocks.api.block.render;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;

/**
 * Helpers for checking whether an {@link IFramedBlock}'s side is occluded by the neighboring block or it occludes
 * a neighboring non-framed block.
 * These helpers must not be used outside the server and client thread, otherwise the {@link FramedBlockEntity}
 * lookups will fail due to safeguards in vanilla code.
 */
public final class CullingHelper
{
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();

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

        // Let the game handle culling against fully solid cubes automatically,
        // prevents xray issues with block tool modifications like farmland tilling
        if (adjState.isSolidRender(level, adjPos))
        {
            return false;
        }

        boolean adjFramed = false;
        if (adjState.getBlock() instanceof IFramedBlock block)
        {
            if (block.shouldPreventNeighborCulling(level, adjPos, adjState, pos, state))
            {
                return false;
            }
            adjFramed = true;
        }

        if (!FramedBlocksAPI.getInstance().detailedCullingEnabled() || !adjFramed)
        {
            if (SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side))
            {
                return compareState(level, pos, adjPos, adjState, adjFramed, side);
            }
            return false;
        }

        SideSkipPredicate pred = ((IFramedBlock) state.getBlock()).getBlockType().getSideSkipPredicate();
        IFramedBlock adjBlock = (IFramedBlock) adjState.getBlock();
        BlockState adjTestState = adjBlock.runOcclusionTestAndGetLookupState(pred, level, pos, state, adjState, side);
        if (adjTestState != null)
        {
            return compareState(level, pos, side, state, adjTestState);
        }
        return false;
    }

    /**
     * Compares the camo state of the {@link FramedBlockEntity} at the given position against the camo state of the
     * {@code FramedBlockEntity} at the given position offset by the given {@link Direction side} or the adjacent
     * state itself if it's not an {@link IFramedBlock}.
     * On the {@code FramedBlockEntity} at the given position, the given side will be used for the camo lookup,
     * on the neighboring {@code FramedBlockEntity}, if applicable, the opposite of that side will be used
     * for the camo lookup
     *
     * @param level The Level
     * @param pos The position of the block being tested
     * @param adjPos The position of the adjacent block
     * @param adjState The adjacent state used in the test, used as the adjacent camo if it's not an {@code IFramedBlock}
     * @param adjFramed Whether the adjacent block is an {@code IFramedBlock}
     * @param side The side on which the neighbor to be tested against is located
     * @return true if the camo states either match (with certain exclusions) or occlude each other by being solid
     */
    public static boolean compareState(
            BlockGetter level, BlockPos pos, BlockPos adjPos, BlockState adjState, boolean adjFramed, Direction side
    )
    {
        BlockState adjCamoState = adjState;
        if (adjFramed)
        {
            if (!(level.getBlockEntity(adjPos) instanceof FramedBlockEntity be))
            {
                return false;
            }
            adjCamoState = be.getCamo(side.getOpposite()).getState();
        }

        BlockState camoState = AIR;
        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            camoState = be.getCamo(side).getState();
        }

        return compareState(level, pos, camoState, adjCamoState, side);
    }

    /**
     * Compares the camo state of the {@link FramedBlockEntity} at the given position against the camo state of the
     * {@code FramedBlockEntity} at the given position offset by the given {@link Direction side}.
     * On the {@code FramedBlockEntity} at the given position, the given testState will be used for the camo lookup,
     * on the neighboring {@code FramedBlockEntity} the given adjTestState will be used for the camo lookup
     *
     * @param level The Level
     * @param pos The position of the block being tested
     * @param side The side on which the neighbor to be tested against is located
     * @param testState The state used in the test, used to look up the camo on the FramedBlockEntity at the given position
     * @param adjTestState The adjacent state used in the test, used to look up the camo in the neighboring FramedBlockEntity
     * @return true if the camo states either match (with certain exclusions) or occlude each other by being solid
     */
    public static boolean compareState(
            BlockGetter level, BlockPos pos, Direction side, BlockState testState, BlockState adjTestState
    )
    {
        BlockState adjCamoState = AIR;
        if (Utils.getBlockEntitySafe(level, pos.relative(side)) instanceof FramedBlockEntity be)
        {
            adjCamoState = be.getCamo(adjTestState).getState();
        }

        if (!adjCamoState.isAir() && Utils.getBlockEntitySafe(level, pos) instanceof FramedBlockEntity be)
        {
            BlockState camoState = be.getCamo(testState).getState();
            return compareState(level, pos, camoState, adjCamoState, side);
        }
        return false;
    }

    /**
     * Compares the two given camo states against each other
     *
     * @param level The Level
     * @param pos The position of the block being tested
     * @param camoState The camo state of the block at the given position
     * @param adjCamoState The camo state of the block at the neighboring position
     * @param side The side on which the neighbor to be tested against is located
     * @return true if the camo states either match (with certain exclusions) or the occluding camo is solid
     */
    public static boolean compareState(
            BlockGetter level, BlockPos pos, BlockState camoState, BlockState adjCamoState, Direction side
    )
    {
        if (camoState.isAir() || adjCamoState.isAir())
        {
            return false;
        }

        if (camoState == adjCamoState)
        {
            return FramedBlocksAPI.getInstance().canCullBlockNextTo(camoState, adjCamoState);
        }
        // Always cull the face if the other camo is solid, even if the camo being culled is non-solid
        return adjCamoState.isSolidRender(level, pos.relative(side));
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
        if (!FramedBlocksAPI.getInstance().canHideNeighborFaceInLevel(level) || adjState.getBlock() instanceof IFramedBlock)
        {
            return false;
        }

        if (block.shouldPreventNeighborCulling(level, pos, state, pos.relative(side), adjState))
        {
            return false;
        }
        if (doesFullFaceOccludeWithCamo(level, pos, state, adjState, side))
        {
            return true;
        }
        if (level.getExistingBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            return be.isSolidSide(side) && !be.isIntangible(null);
        }
        return false;
    }

    private static boolean doesFullFaceOccludeWithCamo(
            BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    )
    {
        if (!(adjState.getBlock() instanceof HalfTransparentBlock))
        {
            return false;
        }

        if (((IFramedBlock) state.getBlock()).getCache(state).isFullFace(side))
        {
            return compareState(level, pos, pos.relative(side), adjState, false, side);
        }
        return false;
    }



    private CullingHelper() { }
}
