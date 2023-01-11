package xfacthd.framedblocks.api.util;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedBlockEntity;

public interface SideSkipPredicate
{
    SideSkipPredicate FALSE = (level, pos, state, adjState, side) -> false;

    SideSkipPredicate CTM = (level, pos, state, adjState, side) ->
    {
        if (!((IFramedBlock) state.getBlock()).getCtmPredicate().test(state, side)) { return false; }

        if (adjState.getBlock() instanceof IFramedBlock block)
        {
            if (!block.getCtmPredicate().test(adjState, side.getOpposite()))
            {
                return false;
            }

            if (Utils.getBlockEntitySafe(level, pos.relative(side)) instanceof FramedBlockEntity be)
            {
                adjState = be.getCamoState(side.getOpposite());
            }
        }

        return !adjState.isAir() && compareState(level, pos, adjState, side, side);
    };

    /**
     * Check whether the given side should be hidden in presence of the given neighbor
     * @param level The level
     * @param pos The blocks position in the level
     * @param state The blocks state
     * @param adjState The neighboring blocks state
     * @param side The side to be checked
     * @return Whether the given side should be hidden
     */
    boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side);

    /**
     * Compares the camo state of the {@link FramedBlockEntity} at the given position against the camo state of the
     * FramedBlockEntity at the given position offset by the given {@link Direction side}.
     * On the FramedBlockEntity at the given position, the given side will be used for the camo lookup, on the
     * neighboring FramedBlockEntity the opposite of the given side will be used for the camo lookup
     * @param level The Level
     * @param pos The position of the block being tested
     * @param side The side on which the neighbor to be tested against is located
     * @return true if the camo states either match (with certain exclusions) or occlude each other by being solid
     */
    static boolean compareState(BlockGetter level, BlockPos pos, Direction side)
    {
        return compareState(level, pos, side, side, side.getOpposite());
    }

    /**
     * Compares the camo state of the {@link FramedBlockEntity} at the given position against the camo state of the
     * FramedBlockEntity at the given position offset by the given {@link Direction side}.
     * On the FramedBlockEntity at the given position, the given side will be used for the camo lookup, on the
     * neighboring FramedBlockEntity the opposite of the given side will be used for the camo lookup
     * @param level The Level
     * @param pos The position of the block being tested
     * @param side The side on which the neighbor to be tested against is located
     * @param camoSide The side on which to look up the camo state in both FramedBlockEntities
     * @return true if the camo states either match (with certain exclusions) or occlude each other by being solid
     * @deprecated Use the overload with two camo side parameters to specify non-standard camo lookup sides
     */
    @Deprecated(forRemoval = true)
    static boolean compareState(BlockGetter level, BlockPos pos, Direction side, Direction camoSide)
    {
        return compareState(level, pos, side, camoSide, camoSide);
    }

    /**
     * Compares the camo state of the {@link FramedBlockEntity} at the given position against the camo state of the
     * FramedBlockEntity at the given position offset by the given {@link Direction side}.
     * @param level The Level
     * @param pos The position of the block being tested
     * @param side The side on which the neighbor to be tested against is located
     * @param camoSide The side on which to look up the camo state in the FramedBlockEntity at the given position
     * @param adjCamoSide The side on which to look up the camo state in the neighboring FramedBlockEntity
     * @return true if the camo states either match (with certain exclusions) or occlude each other by being solid
     */
    static boolean compareState(BlockGetter level, BlockPos pos, Direction side, Direction camoSide, Direction adjCamoSide)
    {
        if (Utils.getBlockEntitySafe(level, pos.relative(side)) instanceof FramedBlockEntity be)
        {
            BlockState adjState = be.getCamoState(adjCamoSide);
            return !adjState.isAir() && compareState(level, pos, adjState, side, camoSide);
        }
        return false;
    }

    /**
     * Compares the camo state of the {@link FramedBlockEntity} at the given position against the given {@link BlockState}
     * @param level The Level
     * @param pos The position of the block being tested
     * @param adjState The neighboring state, can be a camo state from a neighboring FramedBlockEntity or an actual state
     *                 in the level
     * @param camoSide The side on which to look up the camo state in the FramedBlockEntity at the given position
     * @return true if the neighboring state either matches the camo state of the FramedBlockEntity (with certain
     * exclusions) or they occlude each other by being solid
     */
    static boolean compareState(BlockGetter level, BlockPos pos, BlockState adjState, Direction side, Direction camoSide)
    {
        if (Utils.getBlockEntitySafe(level, pos) instanceof FramedBlockEntity be)
        {
            BlockState state = be.getCamoState(camoSide);
            return compareState(level, pos, state, adjState, side);
        }

        return false;
    }

    /**
     * Compares the camo state of the {@link FramedBlockEntity} at the given position against the camo state of the
     * FramedBlockEntity at the given position offset by the given {@link Direction side}.
     * On the FramedBlockEntity at the given position, the given state will be used for the camo lookup, on the
     * neighboring FramedBlockEntity the given adjCamoSide will be used for the camo lookup
     * @param level The Level
     * @param pos The position of the block being tested
     * @param side The side on which the neighbor to be tested against is located
     * @param testState The state used in the test, used to look up the camo on the FramedBlockEntity at the given position
     * @param adjCamoSide The side on which to look up the camo state in the neighboring FramedBlockEntity
     * @return true if the camo states either match (with certain exclusions) or occlude each other by being solid
     */
    static boolean compareState(BlockGetter level, BlockPos pos, Direction side, BlockState testState, Direction adjCamoSide)
    {
        BlockState adjCamoState = Blocks.AIR.defaultBlockState();
        if (Utils.getBlockEntitySafe(level, pos.relative(side)) instanceof FramedBlockEntity be)
        {
            adjCamoState = be.getCamoState(adjCamoSide);
        }

        if (!adjCamoState.isAir() && Utils.getBlockEntitySafe(level, pos) instanceof FramedBlockEntity be)
        {
            BlockState camoState = be.getCamoState(testState);
            return compareState(level, pos, camoState, adjCamoState, side);
        }

        return false;
    }

    /**
     * Compares the camo state of the {@link FramedBlockEntity} at the given position against the camo state of the
     * FramedBlockEntity at the given position offset by the given {@link Direction side}.
     * On the FramedBlockEntity at the given position, the given state will be used for the camo lookup, on the
     * neighboring FramedBlockEntity the given adjCamoSide will be used for the camo lookup
     * @param level The Level
     * @param pos The position of the block being tested
     * @param side The side on which the neighbor to be tested against is located
     * @param testState The state used in the test, used to look up the camo on the FramedBlockEntity at the given position
     * @param adjTestState The adjacent state used in the test, used to look up the camo in the neighboring FramedBlockEntity
     * @return true if the camo states either match (with certain exclusions) or occlude each other by being solid
     */
    static boolean compareState(BlockGetter level, BlockPos pos, Direction side, BlockState testState, BlockState adjTestState)
    {
        BlockState adjCamoState = Blocks.AIR.defaultBlockState();
        if (Utils.getBlockEntitySafe(level, pos.relative(side)) instanceof FramedBlockEntity be)
        {
            adjCamoState = be.getCamoState(adjTestState);
        }

        if (!adjCamoState.isAir() && Utils.getBlockEntitySafe(level, pos) instanceof FramedBlockEntity be)
        {
            BlockState camoState = be.getCamoState(testState);
            return compareState(level, pos, camoState, adjCamoState, side);
        }

        return false;
    }

    /**
     * Compares the two given camo states against each other
     * @param level The Level
     * @param pos The position of the block being tested
     * @param state The camo state of the block at the given position
     * @param adjState The camo state of the block at the neighboring position
     * @param side The side on which the neighbor to be tested against is located
     * @return true if the camo states either match (with certain exclusions) or occlude each other by being solid
     */
    static boolean compareState(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (state.isAir() || adjState.isAir()) { return false; }

        if (state == adjState)
        {
            return FramedBlocksAPI.getInstance().canCullBlockNextTo(state, adjState);
        }
        return state.isSolidRender(level, pos) && adjState.isSolidRender(level, pos.relative(side));
    }
}