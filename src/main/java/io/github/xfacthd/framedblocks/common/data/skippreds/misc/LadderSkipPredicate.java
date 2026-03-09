package io.github.xfacthd.framedblocks.common.data.skippreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.cull.SideSkipPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.skippreds.CullTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

@CullTest(BlockType.FRAMED_LADDER)
public final class LadderSkipPredicate implements SideSkipPredicate
{
    @Override
    @CullTest.TestTarget(BlockType.FRAMED_LADDER)
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        return DirUtils.isY(side) && adjState.getBlock() == FBContent.BLOCK_FRAMED_LADDER.value();
    }
}
