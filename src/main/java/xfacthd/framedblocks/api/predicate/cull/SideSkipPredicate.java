package xfacthd.framedblocks.api.predicate.cull;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import xfacthd.framedblocks.api.block.IFramedBlock;

public interface SideSkipPredicate
{
    SideSkipPredicate FALSE = (level, pos, state, adjState, side) -> false;

    SideSkipPredicate FULL_FACE = (level, pos, state, adjState, side) ->
    {
        if (!((IFramedBlock) state.getBlock()).getCache(state).isFullFace(side))
        {
            return false;
        }

        if (adjState.getBlock() instanceof IFramedBlock block)
        {
            return block.getCache(adjState).isFullFace(side.getOpposite());
        }
        return true;
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
}