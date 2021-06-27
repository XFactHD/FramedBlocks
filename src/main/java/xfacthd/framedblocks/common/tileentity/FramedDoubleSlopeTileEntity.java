package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.SlopeType;
import xfacthd.framedblocks.common.util.Utils;

public class FramedDoubleSlopeTileEntity extends FramedDoubleTileEntity
{
    public FramedDoubleSlopeTileEntity() { super(FBContent.tileTypeDoubleFramedSlab); }

    @Override
    protected boolean hitSecondary(BlockRayTraceResult hit)
    {
        SlopeType type = getBlockState().get(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().get(PropertyHolder.FACING_HOR);
        Direction side = hit.getFace();

        Vector3d vec = Utils.fraction(hit.getHitVec());

        if (type == SlopeType.HORIZONTAL)
        {
            if (side == facing || side == facing.rotateYCCW()) { return false; }
            if (side == facing.getOpposite() || side == facing.rotateY()) { return true; }

            boolean secondary = facing.getAxis() == Direction.Axis.X ? vec.getX() >= vec.getZ() : vec.getZ() >= (1D - vec.getX());

            if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
            return secondary;
        }
        else
        {
            double hor = facing.getAxis() == Direction.Axis.X ? vec.getX() : vec.getZ();
            if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
            {
                hor = 1D - hor;
            }

            if (type == SlopeType.TOP)
            {
                if (side == facing || side == Direction.UP) { return false; }
                if (side == facing.getOpposite() || side == Direction.DOWN) { return true; }
                return vec.getY() <= (1D - hor);
            }
            else if (type == SlopeType.BOTTOM)
            {
                if (side == facing || side == Direction.DOWN) { return false; }
                if (side == facing.getOpposite() || side == Direction.UP) { return true; }
                return vec.getY() >= hor;
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