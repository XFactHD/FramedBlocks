package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.FBContent;

public final class PillarSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (side == null || side.getAxis() != axis)
        {
            return false;
        }

        if (adjState.getBlock() == state.getBlock())
        {
            Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
            return axis == adjAxis;
        }
        else if (adjState.getBlock() == FBContent.BLOCK_FRAMED_HALF_PILLAR.get())
        {
            Direction adjFace = adjState.getValue(BlockStateProperties.FACING);
            return adjFace == side.getOpposite();
        }

        return false;
    }
}