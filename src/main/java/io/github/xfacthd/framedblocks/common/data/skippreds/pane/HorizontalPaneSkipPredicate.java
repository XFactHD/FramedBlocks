package io.github.xfacthd.framedblocks.common.data.skippreds.pane;

import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

@CullTest(BlockType.FRAMED_HORIZONTAL_PANE)
public final class HorizontalPaneSkipPredicate implements SideSkipPredicate {
    @Override
    @CullTest.TestTarget(BlockType.FRAMED_HORIZONTAL_PANE)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side) {
        return adjState.getBlock() == state.getBlock() && !DirUtils.isY(side);
    }
}
