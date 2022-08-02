package xfacthd.framedblocks.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedHorizontalPaneBlock extends FramedBlock
{
    public static final SideSkipPredicate SKIP_PREDICATE = (level, pos, state, adjState, side) ->
    {
        if (adjState.is(FBContent.blockFramedHorizontalPane.get()))
        {
            return SideSkipPredicate.compareState(level, pos, side);
        }
        return false;
    };

    public FramedHorizontalPaneBlock() { super(BlockType.FRAMED_HORIZONTAL_PANE); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.WATERLOGGED);
    }
}
