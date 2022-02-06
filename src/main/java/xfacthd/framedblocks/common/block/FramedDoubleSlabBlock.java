package xfacthd.framedblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.item.FramedDoubleBlockItem;
import xfacthd.framedblocks.common.tileentity.FramedDoubleSlabTileEntity;

import javax.annotation.Nullable;

public class FramedDoubleSlabBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleSlabBlock() { super(BlockType.FRAMED_DOUBLE_SLAB); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return withWater(getDefaultState(), context.getWorld(), context.getPos());
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        return new ItemStack(FBContent.blockFramedSlab.get());
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedDoubleSlabTileEntity(); }

    @Override
    public BlockItem createItemBlock() { return new FramedDoubleBlockItem(this); }
}