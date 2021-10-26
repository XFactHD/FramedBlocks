package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class PostSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction.Axis axis = state.get(BlockStateProperties.AXIS);
        if (side == null || side.getAxis() != axis || adjState.getBlock() != state.getBlock())
        {
            return false;
        }

        Direction.Axis adjAxis = adjState.get(BlockStateProperties.AXIS);
        return axis == adjAxis && SideSkipPredicate.compareState(world, pos, side);
    }
}