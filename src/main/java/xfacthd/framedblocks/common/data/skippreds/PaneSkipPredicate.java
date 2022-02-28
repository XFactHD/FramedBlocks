package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class PaneSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
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

        if (side.getAxis() == Direction.Axis.Y && north == adjNorth && east == adjEast && south == adjSouth && west == adjWest)
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        else if
        (
                (side == Direction.NORTH && north && adjSouth) ||
                (side == Direction.EAST && east && adjWest) ||
                (side == Direction.SOUTH && south && adjNorth) ||
                (side == Direction.WEST && west && adjEast)
        )
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }
        return false;
    }
}