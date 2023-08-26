package xfacthd.framedblocks.common.data.skippreds.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.door.FramedTrapDoorBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_TRAPDOOR)
public final class TrapdoorSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.SingleTarget(BlockType.FRAMED_TRAPDOOR)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction facing = getTrapDoorFacing(state);

        if (side == facing.getOpposite())
        {
            return SideSkipPredicate.FULL_FACE.test(level, pos, state, adjState, side);
        }

        if (adjState.getBlock() instanceof FramedTrapDoorBlock)
        {
            return facing == getTrapDoorFacing(adjState);
        }
        return false;
    }

    private static Direction getTrapDoorFacing(BlockState state)
    {
        if (state.getValue(TrapDoorBlock.OPEN))
        {
            return state.getValue(TrapDoorBlock.FACING);
        }
        else if (state.getValue(TrapDoorBlock.HALF) == Half.BOTTOM)
        {
            return Direction.UP;
        }
        else
        {
            return Direction.DOWN;
        }
    }
}