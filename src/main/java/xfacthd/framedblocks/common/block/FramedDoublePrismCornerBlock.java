package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoublePrismCornerBlock extends FramedDoubleThreewayCornerBlock
{
    public FramedDoublePrismCornerBlock()
    {
        super("framed_double_prism_corner", BlockType.FRAMED_DOUBLE_PRISM_CORNER);
        setDefaultState(getDefaultState().with(PropertyHolder.OFFSET, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(PropertyHolder.OFFSET);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) { return null; }

        state = state.with(PropertyHolder.OFFSET, context.getPos().getY() % 2 == 0);
        return state;
    }
}