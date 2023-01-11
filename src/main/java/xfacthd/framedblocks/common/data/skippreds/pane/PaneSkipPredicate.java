package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class PaneSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() != state.getBlock()) { return false; }

        boolean north = state.getValue(BlockStateProperties.NORTH);
        boolean east = state.getValue(BlockStateProperties.EAST);
        boolean south = state.getValue(BlockStateProperties.SOUTH);
        boolean west = state.getValue(BlockStateProperties.WEST);

        boolean adjNorth = adjState.getValue(BlockStateProperties.NORTH);
        boolean adjEast = adjState.getValue(BlockStateProperties.EAST);
        boolean adjSouth = adjState.getValue(BlockStateProperties.SOUTH);
        boolean adjWest = adjState.getValue(BlockStateProperties.WEST);

        if (Utils.isY(side) && north == adjNorth && east == adjEast && south == adjSouth && west == adjWest)
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if
        (
                (side == Direction.NORTH && north && adjSouth) ||
                (side == Direction.EAST && east && adjWest) ||
                (side == Direction.SOUTH && south && adjNorth) ||
                (side == Direction.WEST && west && adjEast)
        )
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }
}