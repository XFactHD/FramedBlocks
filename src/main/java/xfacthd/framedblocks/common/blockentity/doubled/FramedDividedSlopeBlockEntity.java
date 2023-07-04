package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedDividedSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDividedSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_FRAMED_DIVIDED_SLOPE.get(), pos, state);
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
        if (side == dir)
        {
            return true;
        }
        if (side == dir.getOpposite())
        {
            return false;
        }

        return Utils.fractionInDir(hit.getLocation(), dir) > .5;
    }

    @Override
    protected DoubleBlockTopInteractionMode calculateTopInteractionMode()
    {
        if (getBlockState().getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return DoubleBlockTopInteractionMode.SECOND;
        }
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.HORIZONTAL)
        {
            if (side == Direction.UP)
            {
                return this::getCamoTwo;
            }
            if (side == Direction.DOWN)
            {
                return this::getCamo;
            }

            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            if (side == facing || side == facing.getCounterClockWise())
            {
                if (edge == Direction.UP)
                {
                    return this::getCamoTwo;
                }
                if (edge == Direction.DOWN)
                {
                    return this::getCamo;
                }
            }
        }
        else
        {
            Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
            if (side == facing.getClockWise())
            {
                return this::getCamoTwo;
            }
            if (side == facing.getCounterClockWise())
            {
                return this::getCamo;
            }

            Direction dirTwo = type == SlopeType.TOP ? Direction.UP : Direction.DOWN;
            if (side == facing || side == dirTwo)
            {
                if (edge == facing.getClockWise())
                {
                    return this::getCamoTwo;
                }
                if (edge == facing.getCounterClockWise())
                {
                    return this::getCamo;
                }
            }
        }
        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
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
            return SolidityCheck.BOTH;
        }
        return SolidityCheck.NONE;
    }
}
