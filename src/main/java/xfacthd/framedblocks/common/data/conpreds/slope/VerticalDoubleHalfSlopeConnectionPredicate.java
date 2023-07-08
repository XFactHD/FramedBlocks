package xfacthd.framedblocks.common.data.conpreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class VerticalDoubleHalfSlopeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dir = top ? Direction.UP : Direction.DOWN;

        if (!Utils.isY(side))
        {
            return edge == dir;
        }
        else if (side == dir)
        {
            return edge != null;
        }
        return false;
    }
}
