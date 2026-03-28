package io.github.xfacthd.framedblocks.common.data.skippreds.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public final class CenteredPanelSkipPredicate implements SideSkipPredicate {
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        if (adjState.getBlock() == state.getBlock()) {
            Direction dir = state.getValue(FramedProperties.FACING_NE);
            Direction adjDir = adjState.getValue(FramedProperties.FACING_NE);
            return side.getAxis() != dir.getAxis() && adjDir.getAxis() == dir.getAxis();
        }
        return false;
    }
}
