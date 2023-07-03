package xfacthd.framedblocks.common.data.conpreds.slab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.NonDetailedConnectionPredicate;

public final class DividedSlabConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);

        if ((!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return edge == facing || edge == facing.getOpposite();
        }
        else if ((!top && edge == Direction.DOWN) || (top && edge == Direction.UP))
        {
            return side == facing || side == facing.getOpposite();
        }

        return false;
    }
}
