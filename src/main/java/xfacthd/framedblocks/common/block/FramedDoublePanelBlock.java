package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.HitResult;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.blockentity.FramedDoublePanelBlockEntity;
import xfacthd.framedblocks.common.item.FramedDoubleBlockItem;
import xfacthd.framedblocks.common.util.CtmPredicate;

public class FramedDoublePanelBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        Direction facing = state.getValue(PropertyHolder.FACING_NE);
        return dir == facing || dir == facing.getOpposite();
    };

    public FramedDoublePanelBlock() { super(BlockType.FRAMED_DOUBLE_PANEL); }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_NE);
    }

    @Override //Used by the blueprint
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Direction dir = context.getHorizontalDirection();
        if (dir == Direction.SOUTH || dir == Direction.WEST) { dir = dir.getOpposite(); }
        return defaultBlockState().setValue(PropertyHolder.FACING_NE, dir);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        return new ItemStack(FBContent.blockFramedPanel.get());
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoublePanelBlockEntity(pos, state);
    }

    @Override
    public BlockItem createItemBlock() { return new FramedDoubleBlockItem(this); }
}