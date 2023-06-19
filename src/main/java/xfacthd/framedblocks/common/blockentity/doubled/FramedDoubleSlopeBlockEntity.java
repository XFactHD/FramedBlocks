package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
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
    protected DoubleSoundMode calculateSoundMode()
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
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        return switch (type)
        {
            case HORIZONTAL ->
            {
                if (matchesHor(side, facing) || (Utils.isY(side) && matchesHor(edge, facing)))
                {
                    yield this::getCamo;
                }
                Direction oppFacing = facing.getOpposite();
                if (matchesHor(side, oppFacing) || (Utils.isY(side) && matchesHor(edge, oppFacing)))
                {
                    yield this::getCamoTwo;
                }
                yield EMPTY_GETTER;
            }
            case TOP ->
            {
                if (side.getAxis() == facing.getClockWise().getAxis())
                {
                    if (edge == facing || edge == Direction.UP)
                    {
                        yield this::getCamo;
                    }
                    if (edge == facing.getOpposite() || edge == Direction.DOWN)
                    {
                        yield this::getCamoTwo;
                    }
                    yield EMPTY_GETTER;
                }
                if (side == facing || side == Direction.UP)
                {
                    yield this::getCamo;
                }
                if (side == facing.getOpposite() || side == Direction.DOWN)
                {
                    yield this::getCamoTwo;
                }
                yield EMPTY_GETTER;
            }
            case BOTTOM ->
            {
                if (side.getAxis() == facing.getClockWise().getAxis())
                {
                    if (edge == facing || edge == Direction.DOWN)
                    {
                        yield this::getCamo;
                    }
                    if (edge == facing.getOpposite() || edge == Direction.UP)
                    {
                        yield this::getCamoTwo;
                    }
                    yield EMPTY_GETTER;
                }
                if (side == facing || side == Direction.DOWN)
                {
                    yield this::getCamo;
                }
                if (side == facing.getOpposite() || side == Direction.UP)
                {
                    yield this::getCamoTwo;
                }
                yield EMPTY_GETTER;
            }
        };
    }

    private static boolean matchesHor(Direction side, Direction facing)
    {
        return side == facing || side == facing.getCounterClockWise();
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        return switch (type)
        {
            case HORIZONTAL ->
            {
                if (side == facing || side == facing.getCounterClockWise())
                {
                    yield SolidityCheck.FIRST;
                }
                else if (side == facing.getOpposite() || side == facing.getClockWise())
                {
                    yield SolidityCheck.SECOND;
                }
                yield SolidityCheck.BOTH;
            }
            case TOP ->
            {
                if (side == facing || side == Direction.UP)
                {
                    yield SolidityCheck.FIRST;
                }
                else if (side == facing.getOpposite() || side == Direction.DOWN)
                {
                    yield SolidityCheck.SECOND;
                }
                yield SolidityCheck.BOTH;
            }
            case BOTTOM ->
            {
                if (side == facing || side == Direction.UP)
                {
                    yield SolidityCheck.SECOND;
                }
                else if (side == facing.getOpposite() || side == Direction.DOWN)
                {
                    yield SolidityCheck.FIRST;
                }
                yield SolidityCheck.BOTH;
            }
        };
    }
}