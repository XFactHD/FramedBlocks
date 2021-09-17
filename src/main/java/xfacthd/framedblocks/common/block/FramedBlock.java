package xfacthd.framedblocks.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.AbstractFramedBlock;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.api.util.Utils;

public class FramedBlock extends AbstractFramedBlock
{
    public FramedBlock(BlockType blockType) { this(blockType, IFramedBlock.createProperties()); }

    protected FramedBlock(BlockType blockType, Properties props) { super(blockType, props); }

    protected BlockState withSlopeType(BlockState state, Direction side, Direction facing, Vec3 hitVec)
    {
        state = state.setValue(PropertyHolder.FACING_HOR, facing);

        Vec3 hitPoint = Utils.fraction(hitVec);
        if (side.getAxis() != Direction.Axis.Y)
        {
            if (hitPoint.y() < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (hitPoint.y() > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        if (side == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM);
        }
        else
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);

            boolean xAxis = side.getAxis() == Direction.Axis.X;
            boolean positive = side.getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.z() : hitPoint.x();

            if ((xz > .5D) == positive)
            {
                state = state.setValue(PropertyHolder.FACING_HOR, side.getOpposite().getClockWise());
            }
            else
            {
                state = state.setValue(PropertyHolder.FACING_HOR, side.getOpposite());
            }
        }

        return state;
    }

    protected BlockState withCornerType(BlockState state, BlockPlaceContext context, Direction side, Vec3 hitPoint, Direction facing)
    {
        state = state.setValue(PropertyHolder.FACING_HOR, facing);

        if (side == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.BOTTOM);
        }
        else
        {
            boolean xAxis = context.getClickedFace().getAxis() == Direction.Axis.X;
            boolean positive = context.getClickedFace().getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.z() : hitPoint.x();
            double y = hitPoint.y();

            CornerType type;
            if ((xz > .5D) == positive)
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_RIGHT : CornerType.HORIZONTAL_BOTTOM_RIGHT;
            }
            else
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_LEFT : CornerType.HORIZONTAL_BOTTOM_LEFT;
            }
            state = state.setValue(PropertyHolder.CORNER_TYPE, type);
        }
        return state;
    }
}