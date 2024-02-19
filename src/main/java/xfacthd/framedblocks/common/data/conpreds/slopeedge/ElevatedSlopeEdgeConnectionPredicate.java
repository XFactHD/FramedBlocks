package xfacthd.framedblocks.common.data.conpreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public final class ElevatedSlopeEdgeConnectionPredicate implements ConnectionPredicate
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
            return edge == dirTwo;
        }
        if (side == dirTwo.getOpposite())
        {
            return edge == dir;
        }
        if (side.getAxis() != dir.getAxis() && side.getAxis() != dirTwo.getAxis())
        {
            return edge == dir || edge == dirTwo;
        }

        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction dirTwo = switch (type)
        {
            case BOTTOM -> Direction.DOWN;
            case HORIZONTAL -> dir.getCounterClockWise();
            case TOP -> Direction.UP;
        };

        if (side.getAxis() != dir.getAxis() && side.getAxis() != dirTwo.getAxis())
        {
            return edge == dir.getOpposite() || edge == dirTwo.getOpposite();
        }
        if (type == SlopeType.HORIZONTAL)
        {
            if (side == dir.getOpposite())
            {
                return Utils.isY(edge) || edge == dirTwo.getOpposite();
            }
            if (side == dirTwo.getOpposite())
            {
                return Utils.isY(edge) || edge == dir.getOpposite();
            }
        }
        else
        {
            if (side == dir.getOpposite())
            {
                return edge.getAxis() == dir.getClockWise().getAxis() || edge == dirTwo.getOpposite();
            }
            if (side == dirTwo.getOpposite())
            {
                return edge.getAxis() == dir.getClockWise().getAxis() || edge == dir.getOpposite();
            }
        }
        return false;
    }
}
