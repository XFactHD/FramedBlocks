package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.ISlopeBlock;
import xfacthd.framedblocks.common.data.property.SlopeType;

public final class SlopeConnectionPredicate implements ConnectionPredicate
{
    public static final SlopeConnectionPredicate INSTANCE = new SlopeConnectionPredicate();

    private SlopeConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        ISlopeBlock block = (ISlopeBlock) state.getBlock();
        SlopeType type = block.getSlopeType(state);
        if (type == SlopeType.HORIZONTAL)
        {
            Direction dirOne = state.getValue(FramedProperties.FACING_HOR);
            Direction dirTwo = dirOne.getCounterClockWise();
            if (side == dirOne || side == dirTwo)
            {
                return true;
            }
            if (Utils.isY(side))
            {
                return edge == dirOne || edge == dirTwo;
            }
            return false;
        }

        Direction dirOne = block.getFacing(state);
        Direction dirTwo = type == SlopeType.TOP ? Direction.UP : Direction.DOWN;
        if (side == dirOne || side == dirTwo)
        {
            return true;
        }
        if (side.getAxis() == dirOne.getClockWise().getAxis())
        {
            return edge == dirOne || edge == dirTwo;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        ISlopeBlock block = (ISlopeBlock) state.getBlock();
        SlopeType type = block.getSlopeType(state);
        if (type == SlopeType.HORIZONTAL)
        {
            Direction dirOne = state.getValue(FramedProperties.FACING_HOR).getOpposite();
            Direction dirTwo = dirOne.getCounterClockWise();

            if (side == dirOne || side == dirTwo)
            {
                return Utils.isY(edge);
            }
            return false;
        }

        Direction dirOne = block.getFacing(state).getOpposite();
        Direction dirTwo = type == SlopeType.TOP ? Direction.DOWN : Direction.UP;
        if (side == dirOne || side == dirTwo)
        {
            return edge.getAxis() == dirOne.getClockWise().getAxis();
        }
        return false;
    }
}
