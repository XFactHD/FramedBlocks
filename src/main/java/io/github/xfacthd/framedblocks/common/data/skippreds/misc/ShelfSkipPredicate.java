package io.github.xfacthd.framedblocks.common.data.skippreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;

@CullTest(BlockType.FRAMED_SHELF)
public final class ShelfSkipPredicate implements SideSkipPredicate {
    @Override
    @CullTest.TestTarget(BlockType.FRAMED_SHELF)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.is(state.getBlock())) {
            Direction facing = state.getValue(ShelfBlock.FACING);
            if (side == facing) {
                return false;
            }
            Direction adjFacing = adjState.getValue(ShelfBlock.FACING);
            if (side == facing.getOpposite()) {
                return adjFacing == facing.getOpposite();
            } else {
                return adjFacing == facing;
            }
        }
        return false;
    }
}
