package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class StackedCornerSlopeEdgeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (type.isHorizontal())
        {
            if (side == dir)
            {
                return true;
            }
            Direction xBack = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
            Direction yBack = type.isTop() ? Direction.UP : Direction.DOWN;
            if (side == xBack)
            {
                return edge == dir || edge == yBack;
            }
            if (side == yBack)
            {
                return edge == dir || edge == xBack;
            }
            if (side == xBack.getOpposite() || side == yBack.getOpposite())
            {
                return edge == dir;
            }
        }
        else
        {
            Direction bottom = type == CornerType.TOP ? Direction.UP : Direction.DOWN;
            if (side == bottom)
            {
                return true;
            }
            if (side == dir)
            {
                return edge == bottom || edge == dir.getCounterClockWise();
            }
            if (side == dir.getCounterClockWise())
            {
                return edge == bottom || edge == dir;
            }
            if (side == dir.getOpposite() || side == dir.getClockWise())
            {
                return edge == bottom;
            }
        }
        return false;
    }
}
