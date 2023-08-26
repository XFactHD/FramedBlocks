package xfacthd.framedblocks.common.data.skippreds.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.door.FramedGateBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_GATE)
public final class GateSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.SingleTarget(BlockType.FRAMED_GATE)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction facing = getDoorFacing(state);

        if (side == facing.getOpposite())
        {
            return SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side);
        }

        if (!(adjState.getBlock() instanceof FramedGateBlock))
        {
            return false;
        }

        Direction adjFacing = getDoorFacing(adjState);
        return facing == adjFacing && side.getAxis() != facing.getAxis();
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
