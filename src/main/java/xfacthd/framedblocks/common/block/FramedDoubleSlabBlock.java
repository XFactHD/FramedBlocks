package xfacthd.framedblocks.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.tileentity.FramedDoubleSlabTileEntity;

public class FramedDoubleSlabBlock extends AbstractFramedDoubleBlock
{
    public FramedDoubleSlabBlock() { super("framed_double_slab", BlockType.FRAMED_DOUBLE_SLAB); }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedDoubleSlabTileEntity(); }
}