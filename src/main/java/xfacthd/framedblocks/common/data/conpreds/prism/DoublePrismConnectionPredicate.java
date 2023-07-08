package xfacthd.framedblocks.common.data.conpreds.prism;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.DirectionAxis;

public final class DoublePrismConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return side.getAxis() != dirAxis.axis() || edge != null;
    }
}
