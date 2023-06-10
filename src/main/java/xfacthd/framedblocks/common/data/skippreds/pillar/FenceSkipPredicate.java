package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.common.FBContent;

public final class FenceSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        boolean sameBlock = adjState.getBlock() == state.getBlock();

        if (Utils.isY(side))
        {
            if (sameBlock || isVerticalPostOrLattice(adjState))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, adjState);
            }
            return false;
        }

        if (!hasFenceArm(state, side))
        {
            return false;
        }

        if (sameBlock && hasFenceArm(adjState, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }

        if (adjState.getBlock() == FBContent.BLOCK_FRAMED_FENCE_GATE.get())
        {
            Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (adjDir.getCounterClockWise() == side || adjDir.getClockWise() == side)
            {
                return SideSkipPredicate.compareState(level, pos, side, state, adjState);
            }
        }

        return false;
    }

    private static boolean isVerticalPostOrLattice(BlockState state)
    {
        Block block = state.getBlock();
        if (block == FBContent.BLOCK_FRAMED_LATTICE.get())
        {
            return state.getValue(FramedProperties.Y_AXIS);
        }
        if (block == FBContent.BLOCK_FRAMED_POST.get())
        {
            return state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
        }
        return false;
    }

    private static boolean hasFenceArm(BlockState state, Direction side)
    {
        return switch (side)
        {
            case NORTH -> state.getValue(FenceBlock.NORTH);
            case EAST -> state.getValue(FenceBlock.EAST);
            case SOUTH -> state.getValue(FenceBlock.SOUTH);
            case WEST -> state.getValue(FenceBlock.WEST);
            default -> throw new IllegalArgumentException("Invalid fence arm side: " + side);
        };
    }
}
