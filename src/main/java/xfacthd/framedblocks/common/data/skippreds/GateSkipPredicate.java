package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.block.FramedGateBlock;

public class GateSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction facing = getDoorFacing(state);

        if (side == facing.getOpposite())
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (!(adjState.getBlock() instanceof FramedGateBlock)) { return false; }

        Direction adjFacing = getDoorFacing(adjState);
        if (facing == adjFacing && side.getAxis() != facing.getAxis())
        {
            return SideSkipPredicate.compareState(level, pos, side);
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
