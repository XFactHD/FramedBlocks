package xfacthd.framedblocks.common.data.conpreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class FlatStackedInnerSlopeSlabCornerConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction dirTwo = top ? Direction.UP : Direction.DOWN;

        if (side == facing || side == facing.getCounterClockWise())
        {
            return edge != null && Utils.isY(edge);
        }
        else if (side == dirTwo || (!Utils.isY(side) && edge == dirTwo))
        {
            return true;
        }
        return false;
    }
}
