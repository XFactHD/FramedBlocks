package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.item.FramedDoubleBlockItem;
import xfacthd.framedblocks.common.tileentity.FramedDoublePanelTileEntity;
import xfacthd.framedblocks.common.util.CtmPredicate;

import javax.annotation.Nullable;

public class FramedDoublePanelBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        Direction facing = state.getValue(PropertyHolder.FACING_NE);
        return dir == facing || dir == facing.getOpposite();
    };

    public FramedDoublePanelBlock() { super(BlockType.FRAMED_DOUBLE_PANEL); }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.FACING_NE, BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override //Used by the blueprint
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        Direction dir = context.getHorizontalDirection();
        if (dir == Direction.SOUTH || dir == Direction.WEST) { dir = dir.getOpposite(); }
        return defaultBlockState().setValue(PropertyHolder.FACING_NE, dir);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return new ItemStack(FBContent.blockFramedPanel.get());
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedDoublePanelTileEntity(); }

    @Override
    public BlockItem createItemBlock() { return new FramedDoubleBlockItem(this); }
}