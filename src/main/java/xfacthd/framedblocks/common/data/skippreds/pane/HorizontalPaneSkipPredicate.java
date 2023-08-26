package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_HORIZONTAL_PANE)
public final class HorizontalPaneSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.SingleTarget(BlockType.FRAMED_HORIZONTAL_PANE)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        return adjState.getBlock() == state.getBlock() && !Utils.isY(side);
    }
}
