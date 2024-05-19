package xfacthd.framedblocks.common.data.conpreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class VerticalDividedStairsConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        if ((side == Direction.DOWN && edge == facing) || (side == facing && edge == Direction.DOWN))
        {
            return !type.isBottom() || type == StairsType.BOTTOM_CCW;
        }
        if ((side == Direction.UP && edge == facing) || (side == facing && edge == Direction.UP))
        {
            return !type.isTop() || type == StairsType.TOP_CCW;
        }

        Direction facingCcw = facing.getCounterClockWise();
        if ((side == Direction.DOWN && edge == facingCcw) || (side == facingCcw && edge == Direction.DOWN))
        {
            return !type.isBottom() || type == StairsType.BOTTOM_FWD;
        }
        if ((side == Direction.UP && edge == facingCcw) || (side == facingCcw && edge == Direction.UP))
        {
            return !type.isTop() || type == StairsType.TOP_FWD;
        }
        return false;
    }
}
