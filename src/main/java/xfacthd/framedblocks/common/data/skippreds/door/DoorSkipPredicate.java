package xfacthd.framedblocks.common.data.skippreds.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.common.block.door.FramedDoorBlock;

public final class DoorSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction facing = getDoorFacing(state);

        if (side == facing.getOpposite())
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (!(adjState.getBlock() instanceof FramedDoorBlock))
        {
            return false;
        }

        boolean top = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
        if ((top && side == Direction.DOWN) || (!top && side ==  Direction.UP))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }

        Direction adjFacing = getDoorFacing(adjState);
        if (facing == adjFacing && (side == facing.getClockWise() || side == facing.getCounterClockWise()))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }

        return false;
    }

    private static Direction getDoorFacing(BlockState state)
    {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        DoorHingeSide hinge = state.getValue(BlockStateProperties.DOOR_HINGE);

        if (state.getValue(BlockStateProperties.OPEN))
        {
            return hinge == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
        }

        return facing;
    }
}
