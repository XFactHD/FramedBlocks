package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public final class ElevatedDoubleSlopeEdgeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction dirTwo = switch (type)
        {
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
            case TOP -> Direction.UP;
        };

        if (side == dir || side == dirTwo)
        {
            return true;
        }
        if (side == dir.getOpposite())
        {
            return edge != null && edge.getAxis() == dirTwo.getAxis();
        }
        if (side == dirTwo.getOpposite())
        {
            return edge != null && edge.getAxis() == dir.getAxis();
        }
        return edge == dir || edge == dirTwo;
    }
}
