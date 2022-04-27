package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.SideSkipPredicate;
import xfacthd.framedblocks.common.util.Utils;

public class WallSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.is(FBContent.blockFramedWall.get()))
        {
            if (Utils.isY(side))
            {
                boolean up = state.getValue(WallBlock.UP);
                boolean adjUp = adjState.getValue(WallBlock.UP);
                return up == adjUp && SideSkipPredicate.compareState(world, pos, side);
            }
            else
            {
                return getArm(state, side) == getArm(adjState, side.getOpposite()) && SideSkipPredicate.compareState(world, pos, side);
            }
        }
        return false;
    }

    private static WallHeight getArm(BlockState state, Direction dir)
    {
        switch (dir)
        {
            case NORTH: return state.getValue(WallBlock.NORTH_WALL);
            case EAST:  return state.getValue(WallBlock.EAST_WALL);
            case SOUTH: return state.getValue(WallBlock.SOUTH_WALL);
            case WEST:  return state.getValue(WallBlock.WEST_WALL);
            default: throw new IllegalArgumentException("Invalid wall arm direction");
        }
    }
}
