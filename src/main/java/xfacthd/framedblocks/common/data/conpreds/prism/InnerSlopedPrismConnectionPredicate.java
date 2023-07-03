package xfacthd.framedblocks.common.data.conpreds.prism;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CompoundDirection;

public final class InnerSlopedPrismConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction facing = cmpDir.direction();
        Direction orientation = cmpDir.orientation();

        if (side != facing && side != orientation)
        {
            return true;
        }
        else if (side == orientation)
        {
            return edge != facing;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        return side == cmpDir.direction() && edge == cmpDir.orientation();
    }
}
