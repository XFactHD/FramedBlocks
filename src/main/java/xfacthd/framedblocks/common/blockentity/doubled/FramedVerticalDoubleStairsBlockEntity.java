package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.util.DoubleBlockTopInteractionMode;

public class FramedVerticalDoubleStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalDoubleStairsBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(FBContent.BE_TYPE_FRAMED_VERTICAL_DOUBLE_STAIRS.get(), worldPosition, blockState);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();

        if (side == facing || side == facing.getCounterClockWise())
        {
            return false;
        }

        Vec3 vec = Utils.fraction(hit.getLocation());

        if (side == facing.getOpposite())
        {
            double xz = Utils.isX(facing) ? vec.z : vec.x;
            boolean positive = Utils.isPositive(facing.getCounterClockWise());
            return xz > .5 != positive;
        }
        else if (side == facing.getClockWise())
        {
            double xz = Utils.isX(facing) ? vec.x : vec.z;
            boolean positive = Utils.isPositive(facing);
            return xz > .5 != positive;
        }
        else
        {
            double xz = Utils.isX(facing) ? vec.x : vec.z;
            double xzCCW = Utils.isX(facing) ? vec.z : vec.x;

            boolean positive = Utils.isPositive(facing);
            boolean positiveCCW = Utils.isPositive(facing.getCounterClockWise());

            return (xzCCW > .5 != positiveCCW) && (xz > .5 != positive);
        }
    }

    @Override
    protected DoubleBlockTopInteractionMode calculateTopInteractionMode()
    {
        return DoubleBlockTopInteractionMode.EITHER;
    }

    @Override
    protected CamoGetter getCamoGetter(Direction side, @Nullable Direction edge)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing || side == facing.getCounterClockWise())
        {
            return this::getCamo;
        }
        else if (Utils.isY(side) && (edge == facing || edge == facing.getCounterClockWise()))
        {
            return this::getCamo;
        }
        else if (side == facing.getOpposite())
        {
            if (edge == facing.getClockWise())
            {
                return this::getCamoTwo;
            }
            else if (edge == facing.getCounterClockWise())
            {
                return this::getCamo;
            }
        }
        else if (side == facing.getClockWise())
        {
            if (edge == facing)
            {
                return this::getCamo;
            }
            else if (edge == facing.getOpposite())
            {
                return this::getCamoTwo;
            }
        }

        return EMPTY_GETTER;
    }

    @Override
    protected SolidityCheck getSolidityCheck(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);

        if (side == facing || side == facing.getCounterClockWise())
        {
            return SolidityCheck.FIRST;
        }
        return SolidityCheck.BOTH;
    }
}
