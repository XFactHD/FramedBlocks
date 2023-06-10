package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.camo.EmptyCamoContainer;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_DOUBLE_FRAMED_SLOPE.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (type == SlopeType.HORIZONTAL)
        {
            if (side == facing || side == facing.getCounterClockWise())
            {
                return false;
            }
            if (side == facing.getOpposite() || side == facing.getClockWise())
            {
                return true;
            }

            boolean secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());

            if (Utils.isPositive(facing)) { secondary = !secondary; }
            return secondary;
        }
        else
        {
            double hor = Utils.isX(facing) ? vec.x() : vec.z();
            if (!Utils.isPositive(facing))
            {
                hor = 1D - hor;
            }

            if (type == SlopeType.TOP)
            {
                if (side == facing || side == Direction.UP)
                {
                    return false;
                }
                if (side == facing.getOpposite() || side == Direction.DOWN)
                {
                    return true;
                }
                return vec.y() <= (1D - hor);
            }
            else if (type == SlopeType.BOTTOM)
            {
                if (side == facing || side == Direction.DOWN)
                {
                    return false;
                }
                if (side == facing.getOpposite() || side == Direction.UP)
                {
                    return true;
                }
                return vec.y() >= hor;
            }
        }

        return false;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.BOTTOM)
        {
            return DoubleSoundMode.SECOND;
        }
        else if (type == SlopeType.TOP)
        {
            return DoubleSoundMode.FIRST;
        }
        return DoubleSoundMode.EITHER;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        switch (type)
        {
            case HORIZONTAL ->
            {
                if (side == facing || side == facing.getCounterClockWise())
                {
                    return getCamo();
                }
                if (side == facing.getOpposite() || side == facing.getClockWise())
                {
                    return getCamoTwo();
                }
            }
            case TOP ->
            {
                if (side == facing || side == Direction.UP)
                {
                    return getCamo();
                }
                if (side == facing.getOpposite() || side == Direction.DOWN)
                {
                    return getCamoTwo();
                }
            }
            case BOTTOM ->
            {
                if (side == facing || side == Direction.DOWN)
                {
                    return getCamo();
                }
                if (side == facing.getOpposite() || side == Direction.UP)
                {
                    return getCamoTwo();
                }
            }
        }

        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        CamoContainer camo = getCamo(side);
        if (!camo.isEmpty())
        {
            return camo.isSolid(level, worldPosition);
        }
        return getCamo().isSolid(level, worldPosition) && getCamoTwo().isSolid(level, worldPosition);
    }
}