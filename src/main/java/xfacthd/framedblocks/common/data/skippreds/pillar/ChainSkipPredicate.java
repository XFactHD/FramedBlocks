package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import xfacthd.framedblocks.common.block.pillar.FramedChainBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.skippreds.CullTest;

@CullTest(BlockType.FRAMED_CHAIN)
public final class ChainSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.TestTarget(BlockType.FRAMED_CHAIN)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        return side.getAxis() == state.getValue(FramedChainBlock.AXIS) && adjState == state;
    }
}
