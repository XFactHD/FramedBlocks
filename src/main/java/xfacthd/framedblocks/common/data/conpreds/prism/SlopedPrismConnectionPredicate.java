package xfacthd.framedblocks.common.data.conpreds.prism;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CompoundDirection;

public final class SlopedPrismConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);

        if (side == cmpDir.direction().getOpposite())
        {
            return true;
        }
        else if (side == cmpDir.orientation())
        {
            return edge == cmpDir.direction().getOpposite();
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction facing = cmpDir.direction();
        Direction orientation = cmpDir.orientation();

        if (side == facing || side.getAxis() == facing.getClockWise(orientation.getAxis()).getAxis())
        {
            return edge == orientation;
        }
        return false;
    }
}
