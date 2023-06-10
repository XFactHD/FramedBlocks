package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.common.FBContent;

public final class HalfPillarSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction face = state.getValue(BlockStateProperties.FACING);
        if (side == null || side != face)
        {
            return false;
        }

        if (adjState.getBlock() == state.getBlock())
        {
            Direction adjFace = adjState.getValue(BlockStateProperties.FACING);
            return adjFace == face.getOpposite() && SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (adjState.getBlock() == FBContent.BLOCK_FRAMED_PILLAR.get())
        {
            Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
            return adjAxis == face.getAxis() && SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }

        return false;
    }
}