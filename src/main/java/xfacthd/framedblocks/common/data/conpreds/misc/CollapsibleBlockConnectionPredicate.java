package xfacthd.framedblocks.common.data.conpreds.misc;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class CollapsibleBlockConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction face = state.getValue(PropertyHolder.NULLABLE_FACE).toDirection();
        if (face == null || side == face.getOpposite())
        {
            return true;
        }
        else if (side.getAxis() != face.getAxis())
        {
            return edge == face.getOpposite();
        }

        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        Direction face = state.getValue(PropertyHolder.NULLABLE_FACE).toDirection();
        if (side == face || side.getAxis() != face.getAxis())
        {
            return edge.getAxis() != face.getAxis();
        }
        return false;
    }
}
