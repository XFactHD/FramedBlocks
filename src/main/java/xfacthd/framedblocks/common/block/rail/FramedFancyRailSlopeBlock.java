package xfacthd.framedblocks.common.block.rail;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.predicate.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.function.BiFunction;

class FramedFancyRailSlopeBlock extends FramedRailSlopeBlock
{
    FramedFancyRailSlopeBlock(BlockType type, BiFunction<BlockPos, BlockState, FramedBlockEntity> beFactory)
    {
        super(type, beFactory);
    }

    @Override
    @Nullable
    public BlockState runOcclusionTestAndGetLookupState(
            SideSkipPredicate pred, BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> statePair = AbstractFramedDoubleBlock.getStatePair(adjState);
        return super.runOcclusionTestAndGetLookupState(pred, level, pos, state, statePair.getA(), side);
    }
}
