package xfacthd.framedblocks.api.predicate;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

final class FullEdgeConnectionPredicate extends NonDetailedConnectionPredicate
{
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge)
    {
        return true;
    }
}
