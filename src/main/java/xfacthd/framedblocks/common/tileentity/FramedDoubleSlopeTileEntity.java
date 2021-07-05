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
    public FramedDoubleSlopeTileEntity() { super(FBContent.tileTypeDoubleFramedSlope.get()); }

    @Override
    protected boolean hitSecondary(BlockRayTraceResult hit)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);
        Direction side = hit.getDirection();

        Vector3d vec = Utils.fraction(hit.getLocation());

        if (type == SlopeType.HORIZONTAL)
        {
            if (side == facing || side == facing.getCounterClockWise()) { return false; }
            if (side == facing.getOpposite() || side == facing.getClockWise()) { return true; }

            boolean secondary = facing.getAxis() == Direction.Axis.X ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());

            if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
            return secondary;
        }
        else
        {
            double hor = facing.getAxis() == Direction.Axis.X ? vec.x() : vec.z();
            if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
            {
                hor = 1D - hor;
            }

            if (type == SlopeType.TOP)
            {
                if (side == facing || side == Direction.UP) { return false; }
                if (side == facing.getOpposite() || side == Direction.DOWN) { return true; }
                return vec.y() <= (1D - hor);
            }
            else if (type == SlopeType.BOTTOM)
            {
                if (side == facing || side == Direction.DOWN) { return false; }
                if (side == facing.getOpposite() || side == Direction.UP) { return true; }
                return vec.y() >= hor;
            }
        }

        return false;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (type == SlopeType.HORIZONTAL)
        {
            if (side == facing || side == facing.getCounterClockWise()) { return getCamoState(); }
            if (side == facing.getOpposite() || side == facing.getClockWise()) { return getCamoStateTwo(); }
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

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        BlockState state = getCamoState(side);
        //noinspection deprecation
        if (!state.isAir())
        {
            return state.isSolid();
        }
        return getCamoState().isSolid() && getCamoStateTwo().isSolid();
    }
}