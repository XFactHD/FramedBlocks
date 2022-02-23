package xfacthd.framedblocks.api.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.block.FramedBlockEntity;

public interface SideSkipPredicate
{
    SideSkipPredicate FALSE = (level, pos, state, adjState, side) -> false;

    SideSkipPredicate CTM = (level, pos, state, adjState, side) ->
    {
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

        if (adjState.isAir()) { return false; }

        if (!((IFramedBlock) state.getBlock()).getCtmPredicate().test(state, side)) { return false; }

        return compareState(level, pos, adjState, side);
    };

    /**
     * Check wether the given side should be hidden in presence of the given neighbor
     * @param level The level
     * @param pos The blocks position in the level
     * @param state The blocks state
     * @param adjState The neighboring blocks state
     * @param side The side to be checked
     * @return Wether the given side should be hidden
     */
    boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side);

    static boolean compareState(BlockGetter level, BlockPos pos, Direction side)
    {
        return compareState(level, pos, side, side.getOpposite());
    }

    static boolean compareState(BlockGetter level, BlockPos pos, Direction side, Direction camoSide)
    {
        return compareState(level, pos, side, camoSide, camoSide);
    }

    static boolean compareState(BlockGetter level, BlockPos pos, Direction side, Direction camoSide, Direction adjCamoSide)
    {
        if (Utils.getBlockEntitySafe(level, pos.relative(side)) instanceof FramedBlockEntity be)
        {
            BlockState adjState = be.getCamoState(adjCamoSide);
            if (adjState.isAir()) { return false; }

            return compareState(level, pos, adjState, camoSide);
        }
        return false;
    }

    static boolean compareState(BlockGetter level, BlockPos pos, BlockState adjState, Direction side)
    {
        if (Utils.getBlockEntitySafe(level, pos) instanceof FramedBlockEntity be)
        {
            BlockState state = be.getCamoState(side);
            return (state == adjState && !state.is(BlockTags.LEAVES)) || (state.isSolidRender(level, pos) && adjState.isSolidRender(level, pos.relative(side)));
        }

        return false;
    }
}