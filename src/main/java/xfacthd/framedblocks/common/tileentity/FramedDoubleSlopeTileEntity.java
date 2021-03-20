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
import xfacthd.framedblocks.common.data.SlopeType;

public class FramedDoubleSlopeTileEntity extends FramedDoubleTileEntity
{
    public FramedDoubleSlopeTileEntity() { super(FBContent.tileTypeDoubleFramedSlab); }

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
        SlopeType type = getBlockState().get(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().get(PropertyHolder.FACING_HOR);
        Direction side = hit.getFace();

        Vector3d vec = hit.getHitVec();
        double x = MathHelper.frac(vec.getX());
        double y = MathHelper.frac(vec.getY());
        double z = MathHelper.frac(vec.getZ());

        if (type == SlopeType.HORIZONTAL)
        {
            if (side == facing || side == facing.rotateYCCW()) { return false; }
            if (side == facing.getOpposite() || side == facing.rotateY()) { return true; }

            boolean secondary = facing.getAxis() == Direction.Axis.X ? x >= z : z >= (1D - x);

            if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
            return secondary;
        }
        else
        {
            double hor = facing.getAxis() == Direction.Axis.X ? x : z;
            if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
            {
                hor = 1D - hor;
            }

            if (type == SlopeType.TOP)
            {
                if (side == facing || side == Direction.UP) { return false; }
                if (side == facing.getOpposite() || side == Direction.DOWN) { return true; }
                return y <= (1D - hor);
            }
            else if (type == SlopeType.BOTTOM)
            {
                if (side == facing || side == Direction.DOWN) { return false; }
                if (side == facing.getOpposite() || side == Direction.UP) { return true; }
                return y >= hor;
            }
        }

        return false;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        SlopeType type = getBlockState().get(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().get(PropertyHolder.FACING_HOR);

        if (type == SlopeType.HORIZONTAL)
        {
            if (side == facing || side == facing.rotateYCCW()) { return getCamoState(); }
            if (side == facing.getOpposite() || side == facing.rotateY()) { return getCamoStateTwo(); }
        }
        else if (type == SlopeType.TOP)
        {
            if (side == facing || side == Direction.UP) { return getCamoState(); }
            if (side == facing.getOpposite() || side == Direction.DOWN) { return getCamoStateTwo(); }
        }
        else if (type == SlopeType.BOTTOM)
        {
            if (side == facing || side == Direction.DOWN) { return getCamoState(); }
            if (side == facing.getOpposite() || side == Direction.UP) { return getCamoStateTwo(); }
        }

        return Blocks.AIR.getDefaultState();
    }
}