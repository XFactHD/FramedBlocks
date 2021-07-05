package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.OFFSET);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState state = super.getStateForPlacement(context);
        if (state == null) { return null; }

        state = state.setValue(PropertyHolder.OFFSET, context.getClickedPos().getY() % 2 == 0);
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void attack(BlockState state, World world, BlockPos pos, PlayerEntity player)
    {
        if (world.isClientSide()) { return; }

        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() == FBContent.itemFramedHammer.get())
        {
            world.setBlockAndUpdate(pos, state.setValue(PropertyHolder.OFFSET, !state.getValue(PropertyHolder.OFFSET)));
        }
    }
}