package xfacthd.framedblocks.common.data.conpreds.slab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;

public final class DividedPanelVerticalConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (side == facing)
        {
            return edge == facing.getClockWise() || edge == facing.getCounterClockWise();
        }
        else if (edge == facing)
        {
            return side == facing.getClockWise() || side == facing.getCounterClockWise();
        }
        return false;
    }
}
