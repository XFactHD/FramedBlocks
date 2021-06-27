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
        CornerType type = getBlockState().get(PropertyHolder.CORNER_TYPE);
        Direction facing = getBlockState().get(PropertyHolder.FACING_HOR);
        Direction side = hit.getFace();

        Vector3d vec = Utils.fraction(hit.getHitVec());

        if (type.isHorizontal())
        {
            if (side == facing || (!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP) ||
                (!type.isRight() && side == facing.rotateYCCW()) || (type.isRight() && side == facing.rotateY())
            ) { return false; }

            if (side == facing.getOpposite()) { return true; }

            if (side == Direction.UP || side == Direction.DOWN)
            {
                boolean secondary;
                if (type.isRight())
                {
                    secondary = facing.getAxis() == Direction.Axis.X ? vec.getX() >= (1D - vec.getZ()) : vec.getZ() >= vec.getX();
                }
                else
                {
                   secondary = facing.getAxis() == Direction.Axis.X ? vec.getX() >= vec.getZ() : vec.getZ() >= (1D - vec.getX());
                }

                if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
                return secondary;
            }
            else if (side == facing.rotateY() || side == facing.rotateYCCW())
            {
                double hor = facing.getAxis() == Direction.Axis.X ? vec.getX() : vec.getZ();
                if (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE)
                {
                    hor = 1D - hor;
                }

                boolean secondary;
                if (type.isTop())
                {
                    secondary = vec.getY() <= (1D - hor);
                }
                else
                {
                    secondary = vec.getY() >= hor;
                }

                if (facing.getAxisDirection() == Direction.AxisDirection.POSITIVE) { secondary = !secondary; }
                return secondary;
            }
        }
        else if (type == CornerType.TOP)
        {
            if (side == facing || side == Direction.UP || side == facing.rotateYCCW()) { return false; }
            if (side == Direction.DOWN) { return true; }

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
        }
        else if (type == CornerType.BOTTOM)
        {
            if (side == facing || side == Direction.DOWN || side == facing.rotateYCCW()) { return false; }
            if (side == Direction.UP) { return true; }

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
        }
        return false;
    }

    @Override
    public BlockState getCamoState(Direction side)
    {
        CornerType type = getBlockState().get(PropertyHolder.CORNER_TYPE);
        Direction dir = getBlockState().get(PropertyHolder.FACING_HOR);

        if (type.isHorizontal())
        {
            if (side == dir || (!type.isTop() && side == Direction.DOWN) || (type.isTop() && side == Direction.UP) ||
                (!type.isRight() && side == dir.rotateYCCW()) || (type.isRight() && side == dir.rotateY())
            )
            {
                return getCamoState();
            }

            if (side == dir.getOpposite() || (!type.isTop() && side == Direction.UP) || (type.isTop() && side == Direction.DOWN) ||
                (!type.isRight() && side == dir.rotateY()) || (type.isRight() && side == dir.rotateYCCW())
            )
            {
                return getCamoStateTwo();
            }
        }
        else if (type == CornerType.TOP)
        {
            if (side == dir || side == Direction.UP || side == dir.rotateYCCW()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.DOWN || side == dir.rotateY()) { return getCamoStateTwo(); }
        }
        else if (type == CornerType.BOTTOM)
        {
            if (side == dir || side == Direction.DOWN || side == dir.rotateYCCW()) { return getCamoState(); }
            if (side == dir.getOpposite() || side == Direction.UP || side == dir.rotateY()) { return getCamoStateTwo(); }
        }

        return Blocks.AIR.getDefaultState();
    }
}