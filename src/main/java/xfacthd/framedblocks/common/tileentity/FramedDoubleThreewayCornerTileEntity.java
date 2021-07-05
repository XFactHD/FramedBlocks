package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.util.Utils;

public class FramedDoubleThreewayCornerTileEntity extends FramedDoubleTileEntity
{
    public FramedDoubleThreewayCornerTileEntity() { super(FBContent.tileTypeDoubleFramedThreewayCorner.get()); }

    @Override
    protected boolean hitSecondary(BlockRayTraceResult hit)
    {
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);
        boolean top = getBlockState().getValue(PropertyHolder.TOP);
        Direction side = hit.getDirection();

        Vector3d vec = Utils.fraction(hit.getLocation());

        if (top)
        {
            if (side == facing || side == Direction.UP || side == facing.getCounterClockWise()) { return false; }

            if (side == facing.getClockWise())
            {
                double hor = facing.getAxis() == Direction.Axis.X ? vec.x() : vec.z();
                if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = dir.getAxis() == Direction.Axis.X ? vec.x() : vec.z();
                if (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == Direction.DOWN)
            {
                boolean secondary = facing.getAxis() == Direction.Axis.X ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
                return secondary;
            }
        }
        else
        {
            if (side == facing || side == Direction.DOWN || side == facing.getCounterClockWise()) { return false; }

            if (side == facing.getClockWise())
            {
                double hor = facing.getAxis() == Direction.Axis.X ? vec.x() : vec.z();
                if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = dir.getAxis() == Direction.Axis.X ? vec.x() : vec.z();
                if (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == Direction.UP)
            {
                boolean secondary = facing.getAxis() == Direction.Axis.X ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
                return secondary;
            }
        }
        return false;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction dir = getBlockState().getValue(PropertyHolder.FACING_HOR);
        boolean top = getBlockState().getValue(PropertyHolder.TOP);

        if (top)
        {
            if (side == dir || side == Direction.UP || side == dir.getCounterClockWise()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.DOWN || side == dir.getClockWise()) { return getCamoStateTwo(); }
        }
        else
        {
            if (side == dir || side == Direction.DOWN || side == dir.getCounterClockWise()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.UP || side == dir.getClockWise()) { return getCamoStateTwo(); }
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction dir = getBlockState().get(PropertyHolder.FACING_HOR);
        boolean top = getBlockState().get(PropertyHolder.TOP);

        if (side == dir || side == dir.rotateYCCW() || (side == Direction.DOWN && !top) || (side == Direction.UP && top))
        {
            return getCamoState(side).isSolid();
        }
        return getCamoState().isSolid() && getCamoStateTwo().isSolid();
    }
}