package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public final class DoubleSlopeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (type == SlopeType.HORIZONTAL)
        {
            return !Utils.isY(side) || edge != null;
        }

        Direction dirOne = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = type == SlopeType.TOP ? Direction.UP : Direction.DOWN;
        if (side.getAxis() == dirOne.getAxis() || side.getAxis() == dirTwo.getAxis())
        {
            return true;
        }
        return edge != null;
    }
}
