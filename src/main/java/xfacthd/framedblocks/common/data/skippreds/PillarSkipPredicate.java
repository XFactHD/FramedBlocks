package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class PillarSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction.Axis axis = state.get(BlockStateProperties.AXIS);
        if (side == null || side.getAxis() != axis)
        {
            return false;
        }

        if (adjState.getBlock() == state.getBlock())
        {
            Direction.Axis adjAxis = adjState.get(BlockStateProperties.AXIS);
            return axis == adjAxis && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedHalfPillar.get())
        {
            Direction adjFace = adjState.get(BlockStateProperties.FACING);
            return adjFace == side.getOpposite() && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }
}