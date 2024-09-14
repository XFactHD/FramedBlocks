package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class StackedInnerCornerSlopeEdgeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (type.isHorizontal())
        {
            Direction xBack = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
            Direction yBack = type.isTop() ? Direction.UP : Direction.DOWN;
            if (side == dir || side == xBack || side == yBack)
            {
                return true;
            }
            if (side == dir.getOpposite())
            {
                return edge == xBack || edge == yBack;
            }
            if (side == xBack.getOpposite())
            {
                return edge == dir || edge == yBack;
            }
            if (side == yBack.getOpposite())
            {
                return edge == dir || edge == xBack;
            }
        }
        else
        {
            Direction bottom = type == CornerType.TOP ? Direction.UP : Direction.DOWN;
            if (side == bottom || side == dir || side == dir.getCounterClockWise())
            {
                return true;
            }
            if (side == bottom.getOpposite())
            {
                return edge == dir || edge == dir.getCounterClockWise();
            }
            if (side == dir.getClockWise())
            {
                return edge == bottom || edge == dir;
            }
            if (side == dir.getOpposite())
            {
                return edge == bottom || edge == dir.getCounterClockWise();
            }
        }
        return false;
    }
}
