package xfacthd.framedblocks.common.blockentity.doubled;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.FramedDoubleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;
import xfacthd.framedblocks.api.util.Utils;

public class FramedDoubleSlopeBlockEntity extends FramedDoubleBlockEntity
{
    public FramedDoubleSlopeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.BE_TYPE_DOUBLE_FRAMED_SLOPE.value(), pos, state);
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
}