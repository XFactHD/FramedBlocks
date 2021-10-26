package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;

public class PostSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (side == null || side.getAxis() != axis || adjState.getBlock() != state.getBlock())
        {
            return false;
        }

        Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
        return axis == adjAxis && SideSkipPredicate.compareState(level, pos, side);
    }
}