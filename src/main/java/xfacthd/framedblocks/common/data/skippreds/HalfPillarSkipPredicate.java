package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class HalfPillarSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction face = state.get(BlockStateProperties.FACING);
        if (side == null || side != face)
        {
            return false;
        }

        if (adjState.getBlock() == state.getBlock())
        {
            Direction adjFace = adjState.get(BlockStateProperties.FACING);
            return adjFace == face.getOpposite() && SideSkipPredicate.compareState(world, pos, side);
        }
        else if (adjState.getBlock() == FBContent.blockFramedPillar.get())
        {
            Direction.Axis adjAxis = adjState.get(BlockStateProperties.AXIS);
            return adjAxis == face.getAxis() && SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }
}