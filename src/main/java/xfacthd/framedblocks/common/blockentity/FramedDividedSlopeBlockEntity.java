package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDividedSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDividedSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedDividedSlope.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction dir;
        if (getBlockState().getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            dir = Direction.UP;
        }
        else
        {
            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            dir = facing.getClockWise();
        }

        Direction side = hit.getDirection();
        if (side == dir) { return true; }
        if (side == dir.getOpposite()) { return false; }

        return Utils.fractionInDir(hit.getLocation(), dir) > .5;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        if (getBlockState().getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return DoubleSoundMode.SECOND;
        }
        return DoubleSoundMode.EITHER;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.HORIZONTAL)
        {
            if (side == Direction.UP)
            {
                return getCamoTwo();
            }
            if (side == Direction.DOWN)
            {
                return getCamo();
            }
        }
        else
        {
            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            if (side == facing.getClockWise())
            {
                return getCamoTwo();
            }
            if (side == facing.getCounterClockWise())
            {
                return getCamo();
            }
        }
        return EmptyCamoContainer.EMPTY;
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        Direction secDir = switch (type)
        {
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> facing.getCounterClockWise();
        };

        if (side == facing || side == secDir)
        {
            return getCamo().isSolid(level, worldPosition) && getCamoTwo().isSolid(level, worldPosition);
        }

        return false;
    }
}
