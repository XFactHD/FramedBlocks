package io.github.xfacthd.framedblocks.common.blockentity.doubled.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.MathUtils;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class FramedDoubleThreewayCornerBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleThreewayCornerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_DOUBLE_FRAMED_THREEWAY_CORNER.value(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit, Vec3 lookVec, Vec3 eyePos)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        boolean top = getBlockState().getValue(FramedProperties.TOP);
        Direction side = hit.getDirection();

        Vec3 vec = MathUtils.fraction(hit.getLocation());

        if (top)
        {
            if (side == facing || side == Direction.UP || side == facing.getCounterClockWise())
            {
                return false;
            }

            if (side == facing.getClockWise())
            {
                double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
                if (!DirUtils.isPositive(facing))
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = DirUtils.isX(dir) ? vec.x() : vec.z();
                if (!DirUtils.isPositive(dir))
                {
                    hor = 1D - hor;
                }
                return vec.y() <= (1D - hor);
            }
            else if (side == Direction.DOWN)
            {
                boolean secondary = DirUtils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (DirUtils.isPositive(facing))
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
                double hor = DirUtils.isX(facing) ? vec.x() : vec.z();
                if (!DirUtils.isPositive(facing))
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == facing.getOpposite())
            {
                Direction dir = facing.getCounterClockWise();
                double hor = DirUtils.isX(dir) ? vec.x() : vec.z();
                if (!DirUtils.isPositive(dir))
                {
                    hor = 1D - hor;
                }
                return vec.y() >= hor;
            }
            else if (side == Direction.UP)
            {
                boolean secondary = DirUtils.isX(facing) ? vec.x() >= vec.z() : vec.z() >= (1D - vec.x());
                if (DirUtils.isPositive(facing))
                {
                    secondary = !secondary;
                }
                return secondary;
            }
        }
        return false;
    }
}
