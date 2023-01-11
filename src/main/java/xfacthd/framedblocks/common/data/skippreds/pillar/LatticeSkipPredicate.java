package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;

public final class LatticeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Block block = adjState.getBlock();
        if (block == state.getBlock() && hasArm(state, side) && hasArm(adjState, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (Utils.isY(side) && hasArm(state, side) && isFenceOrVerticalLattice(block, adjState))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean isFenceOrVerticalLattice(Block block, BlockState state)
    {
        if (block == FBContent.blockFramedPost.get())
        {
            return state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
        }
        return block == FBContent.blockFramedFence.get();
    }

    private static boolean hasArm(BlockState state, Direction side)
    {
        return switch (side.getAxis())
        {
            case X -> state.getValue(FramedProperties.X_AXIS);
            case Y -> state.getValue(FramedProperties.Y_AXIS);
            case Z -> state.getValue(FramedProperties.Z_AXIS);
        };
    }
}
