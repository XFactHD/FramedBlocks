package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class ElevatedDoubleCornerSlopeEdgeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        Direction baseFace = switch (type)
        {
            case BOTTOM -> Direction.DOWN;
            case TOP -> Direction.UP;
            default -> dir;
        };
        if (side == baseFace || edge == baseFace)
        {
            return true;
        }
        Direction xBack;
        Direction yBack;
        if (type.isHorizontal())
        {
            xBack = type.isRight() ? dir.getClockWise() : dir.getCounterClockWise();
            yBack = type.isTop() ? Direction.UP : Direction.DOWN;
        }
        else
        {
            xBack = dir;
            yBack = dir.getCounterClockWise();
        }
        if ((side == xBack && edge == yBack) || (side == yBack && edge == xBack))
        {
            return true;
        }
        if ((side == xBack.getOpposite() || side == yBack.getOpposite()) && edge == baseFace.getOpposite())
        {
            return true;
        }
        if (side == baseFace.getOpposite() && (edge == xBack.getOpposite() || edge == yBack.getOpposite()))
        {
            return true;
        }
        return false;
    }
}
