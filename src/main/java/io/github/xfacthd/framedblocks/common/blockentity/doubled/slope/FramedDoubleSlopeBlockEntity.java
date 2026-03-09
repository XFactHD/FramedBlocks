package io.github.xfacthd.framedblocks.common.blockentity.doubled.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedDoubleSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_DOUBLE_FRAMED_SLOPE.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        SlopeType type = getBlockState().getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        Direction side = hit.getDirection();

        Vec3 vec = MathUtils.fraction(hit.getLocation());

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

            boolean secondary = DirUtils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());

            if (DirUtils.isPositive(facing)) { secondary = !secondary; }
            return secondary;
        }
        else
        {
            double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
            if (!DirUtils.isPositive(facing))
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
}