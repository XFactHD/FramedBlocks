package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;

public final class FenceSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (!Utils.isY(side) && !hasFenceArm(state, side))
        {
            return false;
        }

        if (adjState.getBlock() == state.getBlock() && hasFenceArm(adjState, side.getOpposite()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }

        if (adjState.getBlock() == FBContent.blockFramedFenceGate.get())
        {
            Direction adjDir = adjState.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (adjDir.getCounterClockWise() == side || adjDir.getClockWise() == side)
            {
                return SideSkipPredicate.compareState(level, pos, side);
            }
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
