package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import xfacthd.framedblocks.common.FBContent;

public class FramedDoubleSlopeTileEntity extends FramedDoubleTileEntity
{
    public FramedDoubleSlopeTileEntity() { super(FBContent.tileTypeDoubleFramedSlab); }

    @Override
    protected void applyCamo(ItemStack camoStack, BlockState camoState, BlockRayTraceResult hit)
    {
        //TODO: implement
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        return Blocks.AIR.getDefaultState(); //TODO: implement
    }
}