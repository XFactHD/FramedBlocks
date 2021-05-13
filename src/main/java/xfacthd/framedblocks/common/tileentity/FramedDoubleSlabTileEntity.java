package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import xfacthd.framedblocks.common.FBContent;

public class FramedDoubleSlabTileEntity extends FramedDoubleTileEntity
{
    public FramedDoubleSlabTileEntity() { super(FBContent.tileTypeDoubleFramedSlab); }

    @Override
    protected BlockState getCamoState(BlockRayTraceResult hit)
    {
        return hitSecondary(hit) ? getCamoStateTwo() : getCamoState();
    }

    @Override
    protected ItemStack getCamoStack(BlockRayTraceResult hit)
    {
        return hitSecondary(hit) ? getCamoStackTwo() : getCamoStack();
    }

    @Override
    protected void applyCamo(ItemStack camoStack, BlockState camoState, BlockRayTraceResult hit)
    {
        if (hitSecondary(hit))
        {
            this.camoStack = camoStack;
            this.camoState = camoState;
        }
        else
        {
            super.applyCamo(camoStack, camoState, hit);
        }
    }

    private boolean hitSecondary(BlockRayTraceResult hit)
    {
        return hit.getFace() == Direction.UP || MathHelper.frac(hit.getHitVec().getY()) >= .5F;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        if (side == Direction.UP) { return getCamoStateTwo(); }
        if (side == Direction.DOWN) { return getCamoState(); }
        return Blocks.AIR.getDefaultState();
    }
}