package xfacthd.framedblocks.common.data.skippreds;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.SideSkipPredicate;

public class DoorSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(IBlockReader world, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (!adjState.matchesBlock(FBContent.blockFramedDoor.get())) { return false; }

        boolean top = state.get(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER;
        if ((top && side == Direction.DOWN) || (!top && side ==  Direction.UP))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        Direction facing = getDoorFacing(state);
        Direction adjFacing = getDoorFacing(adjState);
        if (facing == adjFacing && (side == facing.rotateY() || side == facing.rotateYCCW()))
        {
            return SideSkipPredicate.compareState(world, pos, side);
        }

        return false;
    }

    private static Direction getDoorFacing(BlockState state)
    {
        Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
        DoorHingeSide hinge = state.get(BlockStateProperties.DOOR_HINGE);

        if (state.get(BlockStateProperties.OPEN))
        {
            return hinge == DoorHingeSide.LEFT ? facing.rotateY() : facing.rotateYCCW();
        }

        return facing;
    }
}
