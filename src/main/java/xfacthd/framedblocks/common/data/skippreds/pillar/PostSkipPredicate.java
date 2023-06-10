package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.common.FBContent;

public final class PostSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (side == null || side.getAxis() != axis)
        {
            return false;
        }

        Block block = adjState.getBlock();
        if (block == state.getBlock())
        {
            Direction.Axis adjAxis = adjState.getValue(BlockStateProperties.AXIS);
            return axis == adjAxis && SideSkipPredicate.compareState(level, pos, side, state, side);
        }
        else if (block == FBContent.BLOCK_FRAMED_FENCE.get() || isVerticalLattice(block, adjState))
        {
            return axis == Direction.Axis.Y && SideSkipPredicate.compareState(level, pos, side, state, side);
        }
        return false;
    }

    private static boolean isVerticalLattice(Block block, BlockState state)
    {
        return block == FBContent.BLOCK_FRAMED_LATTICE.get() && state.getValue(FramedProperties.Y_AXIS);
    }
}