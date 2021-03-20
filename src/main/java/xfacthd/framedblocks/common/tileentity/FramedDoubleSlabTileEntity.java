package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;

public class FramedDoubleSlabTileEntity extends FramedDoubleTileEntity
{
    public FramedDoubleSlabTileEntity() { super(FBContent.tileTypeDoubleFramedSlab); }

    @Override
    protected BlockState getCamoState(BlockRayTraceResult hit)
    {
        return MathHelper.frac(hit.getHitVec().getY()) >= .5F ? getCamoStateTwo() : getCamoState();
    }

    @Override
    protected void applyCamo(ItemStack camoStack, BlockState camoState, BlockRayTraceResult hit)
    {
        Vector3d vec = hit.getHitVec();
        if (MathHelper.frac(vec.getY()) >= .5F)
        {
            this.camoStack = camoStack;
            this.camoState = camoState;
        }
        else
        {
            super.applyCamo(camoStack, camoState, hit);
        }
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        if (side == Direction.UP) { return getCamoStateTwo(); }
        if (side == Direction.DOWN) { return getCamoState(); }
        return Blocks.AIR.getDefaultState();
    }
}