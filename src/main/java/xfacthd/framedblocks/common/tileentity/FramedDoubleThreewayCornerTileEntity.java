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
        Direction facing = getBlockState().get(PropertyHolder.FACING_HOR);
        boolean top = getBlockState().get(PropertyHolder.TOP);
        Direction side = hit.getFace();

        Vector3d vec = Utils.fraction(hit.getHitVec());

        if (top)
        {
            if (side == facing || side == Direction.UP || side == facing.rotateYCCW()) { return false; }

            if (side == facing.rotateY())
            {
                double hor = facing.getAxis() == Direction.Axis.X ? vec.getX() : vec.getZ();
                if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }
                return vec.getY() <= (1D - hor);
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.rotateYCCW();
                double hor = dir.getAxis() == Direction.Axis.X ? vec.getX() : vec.getZ();
                if (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }
                return vec.getY() <= (1D - hor);
            }
            else if (side == Direction.DOWN)
            {
                boolean secondary = facing.getAxis() == Direction.Axis.X ? vec.getX() >= vec.getZ() : vec.getZ() >= (1D - vec.getX());
                if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
                return secondary;
            }
        }
        else
        {
            if (side == facing || side == Direction.DOWN || side == facing.rotateYCCW()) { return false; }

            if (side == facing.rotateY())
            {
                double hor = facing.getAxis() == Direction.Axis.X ? vec.getX() : vec.getZ();
                if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }
                return vec.getY() >= hor;
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.rotateYCCW();
                double hor = dir.getAxis() == Direction.Axis.X ? vec.getX() : vec.getZ();
                if (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }
                return vec.getY() >= hor;
            }
            else if (side == Direction.UP)
            {
                boolean secondary = facing.getAxis() == Direction.Axis.X ? vec.getX() >= vec.getZ() : vec.getZ() >= (1D - vec.getX());
                if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
                return secondary;
            }
        }
        return false;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        Direction dir = getBlockState().get(PropertyHolder.FACING_HOR);
        boolean top = getBlockState().get(PropertyHolder.TOP);

        if (top)
        {
            if (side == dir || side == Direction.UP || side == dir.rotateYCCW()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.DOWN || side == dir.rotateY()) { return getCamoStateTwo(); }
        }
        else
        {
            if (side == dir || side == Direction.DOWN || side == dir.rotateYCCW()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.UP || side == dir.rotateY()) { return getCamoStateTwo(); }
        }

        return Blocks.AIR.getDefaultState();
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