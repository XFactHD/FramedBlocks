package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedDoubleThreewayCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleThreewayCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_DOUBLE_FRAMED_THREEWAY_CORNER.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (top)
        {
            if (side == facing || side == Direction.UP || side == facing.getCounterClockWise())
            {
                return false;
            }

            if (side == facing.getClockWise())
            {
                double hor = Utils.isX(facing) ? vec.x() : vec.z();
                if (!Utils.isPositive(facing))
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = Utils.isX(dir) ? vec.x() : vec.z();
                if (!Utils.isPositive(dir))
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == Direction.DOWN)
            {
                boolean secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (Utils.isPositive(facing))
                {
                    secondary = !secondary;
                }
                return secondary;
            }
        }
        else
        {
            if (side == facing || side == Direction.DOWN || side == facing.getCounterClockWise())
            {
                return false;
            }

            if (side == facing.getClockWise())
            {
                double hor = Utils.isX(facing) ? vec.x() : vec.z();
                if (!Utils.isPositive(facing))
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = Utils.isX(dir) ? vec.x() : vec.z();
                if (!Utils.isPositive(dir))
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == Direction.UP)
            {
                boolean secondary = Utils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (Utils.isPositive(facing))
                {
                    secondary = !secondary;
                }
                return secondary;
            }
        }
        return false;
    }

    @Override
    protected DoubleSoundMode calculateSoundMode()
    {
        return getBlockState().getValue(FramedProperties.TOP) ? DoubleSoundMode.FIRST : DoubleSoundMode.EITHER;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing || side == facing.getCounterClockWise() || side == dirTwo)
        {
            return this::getCamo;
        }
        else if (side == dirTwo.getOpposite())
        {
            if (edge == facing || edge == facing.getCounterClockWise())
            {
                return this::getCamo;
            }
            else if (edge == facing.getOpposite() || edge == facing.getClockWise())
            {
                return this::getCamoTwo;
            }
        }
        else if (side == facing.getOpposite())
        {
            if (edge == facing.getCounterClockWise() || edge == dirTwo)
            {
                return this::getCamo;
            }
            else if (edge == facing.getClockWise() || edge == dirTwo.getOpposite())
            {
                return this::getCamoTwo;
            }
        }
        else if (side == facing.getClockWise())
        {
            if (edge == facing || edge == dirTwo)
            {
                return this::getCamo;
            }
            else if (edge == facing.getOpposite() || edge == dirTwo.getOpposite())
            {
                return this::getCamoTwo;
            }
        }

        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        Direction dir = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);

        if (side == dir || side == dir.getCounterClockWise() || (side == Direction.DOWN && !top) || (side == Direction.UP && top))
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.BOTH;
    }
}