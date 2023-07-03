package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class VerticalDividedStairsConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        if (side == facing || side == facing.getCounterClockWise())
        {
            return edge != null && Utils.isY(edge);
        }
        else if (Utils.isY(side))
        {
            return edge == facing || edge == facing.getCounterClockWise();
        }
        return false;
    }
}
