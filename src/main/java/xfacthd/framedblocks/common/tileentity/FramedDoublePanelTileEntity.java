package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedDoublePanelTileEntity extends FramedDoubleTileEntity
{
    public FramedDoublePanelTileEntity() { super(FBContent.tileTypeDoubleFramedSlab); }

    @Override
    protected BlockState getCamoState(BlockRayTraceResult hit)
    {
        return hitSecondary(hit) ? getCamoStateTwo() : getCamoState();
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
        Direction facing = getBlockState().get(PropertyHolder.FACING_NE);
        Direction side = hit.getFace();
        Vector3d vec = hit.getHitVec();

        if (side == facing) { return false; }
        if (side == facing.getOpposite()) { return true; }

        if (facing == Direction.NORTH)
        {
            return MathHelper.frac(vec.getZ()) > .5F;
        }
        else
        {
            return MathHelper.frac(vec.getX()) <= .5F;
        }
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction facing = getBlockState().get(PropertyHolder.FACING_NE);
        if (side == facing) { return getCamoState(); }
        if (side == facing.getOpposite()) { return getCamoStateTwo(); }
        return Blocks.AIR.getDefaultState();
    }
}