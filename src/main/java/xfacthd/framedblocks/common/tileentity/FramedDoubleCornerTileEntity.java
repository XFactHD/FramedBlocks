package xfacthd.framedblocks.common.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.util.Utils;

public class FramedDoubleCornerTileEntity extends FramedDoubleTileEntity
{
    public FramedDoubleCornerTileEntity() { super(FBContent.tileTypeDoubleFramedCorner.get()); }

    @Override
    protected boolean hitSecondary(BlockRayTraceResult hit)
    {
        CornerType type = getBlockState().getValue(PropertyHolder.CORNER_TYPE);
        Direction facing = getBlockState().getValue(PropertyHolder.FACING_HOR);
        Direction side = hit.getDirection();

        Vector3d vec = Utils.fraction(hit.getLocation());

        if (type.isHorizontal())
        {
            if (side == facing || (!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP) ||
                (!type.isRight() && side == facing.getCounterClockWise()) || (type.isRight() && side == facing.getClockWise())
            ) { return false; }

            if (side == facing.getOpposite()) { return true; }

            if (side == Direction.UP || side == Direction.DOWN)
            {
                boolean secondary;
                if (type.isRight())
                {
                    secondary = facing.getAxis() == Direction.Axis.X ? vec.x() >= (1D - vec.z()) : vec.z() >= vec.x();
                }
                else
                {
                   secondary = facing.getAxis() == Direction.Axis.X ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                }

                if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
                return secondary;
            }
            else if (side == facing.getClockWise() || side == facing.getCounterClockWise())
            {
                double hor = facing.getAxis() == Direction.Axis.X ? vec.x() : vec.z();
                if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }

                boolean secondary;
                if (type.isTop())
                {
                    secondary = vec.y() <= (1D - hor);
                }
                else
                {
                    secondary = vec.y() >= hor;
                }

                return secondary;
            }
        }
        else if (type == CornerType.TOP)
        {
            if (side == facing || side == Direction.UP || side == facing.getCounterClockWise()) { return false; }
            if (side == Direction.DOWN) { return true; }

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
        }
        else if (type == CornerType.BOTTOM)
        {
            if (side == facing || side == Direction.DOWN || side == facing.getCounterClockWise()) { return false; }
            if (side == Direction.UP) { return true; }

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
        }
        return false;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        CornerType type = getBlockState().getValue(PropertyHolder.CORNER_TYPE);
        Direction dir = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (type.isHorizontal())
        {
            if (side == dir || (!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP) ||
                (!type.isRight() && side == dir.getCounterClockWise()) || (type.isRight() && side == dir.getClockWise())
            )
            {
                return getCamoState();
            }

            if (side == dir.getOpposite() || (!type.isTop() && side == Direction.UP) || (type.isTop() && side == Direction.DOWN) ||
                (!type.isRight() && side == dir.getClockWise()) || (type.isRight() && side == dir.getCounterClockWise())
            )
            {
                return getCamoStateTwo();
            }
        }
        else if (type == CornerType.TOP)
        {
            if (side == dir || side == Direction.UP || side == dir.getCounterClockWise()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.DOWN || side == dir.getClockWise()) { return getCamoStateTwo(); }
        }
        else if (type == CornerType.BOTTOM)
        {
            if (side == dir || side == Direction.DOWN || side == dir.getCounterClockWise()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.UP || side == dir.getClockWise()) { return getCamoStateTwo(); }
        }

        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        CornerType type = getBlockState().getValue(PropertyHolder.CORNER_TYPE);
        Direction dir = getBlockState().getValue(PropertyHolder.FACING_HOR);

        if (type.isHorizontal())
        {
            if ((!type.isRight() && side == dir.getCounterClockWise()) || (type.isRight() && side == dir.getClockWise()) ||
                (!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP) ||
                side == dir || side == dir.getOpposite()
            )
            {
                //noinspection ConstantConditions
                return getCamoState(side).isSolidRender(level, worldPosition);
            }
        }
        else if ((side == dir || side == dir.getCounterClockWise() || side.getAxis() == Direction.Axis.Y))
        {
            //noinspection ConstantConditions
            return getCamoState(side).isSolidRender(level, worldPosition);
        }

        //noinspection ConstantConditions
        return getCamoState().isSolidRender(level, worldPosition) && getCamoStateTwo().isSolidRender(level, worldPosition);
    }
}