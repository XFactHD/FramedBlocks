package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoublePrismCornerBlock extends FramedDoubleThreewayCornerBlock
{
    public FramedDoublePrismCornerBlock()
    {
        super(BlockType.FRAMED_DOUBLE_PRISM_CORNER);
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.OFFSET, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.OFFSET);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) { return null; }

        state = state.setValue(PropertyHolder.OFFSET, context.getClickedPos().getY() % 2 == 0);
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void attack(BlockState state, Level world, BlockPos pos, Player player)
    {
        if (world.isClientSide()) { return; }

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == FBContent.itemFramedHammer.get())
        {
            world.setBlockAndUpdate(pos, state.setValue(PropertyHolder.OFFSET, !state.getValue(PropertyHolder.OFFSET)));
        }
    }
}