package xfacthd.framedblocks.common.data.conpreds.misc;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import xfacthd.framedblocks.common.block.cube.FramedCollapsibleCopycatBlock;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class CollapsibleCopycatConnectionPredicate implements ConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        int solid = state.getValue(PropertyHolder.SOLID_FACES);
        if ((solid & (1 << side.ordinal())) != 0)
        {
            int mask = ~(1 << side.getOpposite().ordinal());
            if (edge != null)
            {
                mask &= ~(1 << edge.getOpposite().ordinal());
            }
            return (solid & mask) == (mask & FramedCollapsibleCopycatBlock.ALL_SOLID);
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge)
    {
        int solid = state.getValue(PropertyHolder.SOLID_FACES);
        return (solid & (1 << edge.ordinal())) != 0;
    }
}
