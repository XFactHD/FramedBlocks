package io.github.xfacthd.framedblocks.common.data.conpreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.block.cube.FramedCollapsibleCubeBlock;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class CollapsibleCubeConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        int solid = state.getValue(PropertyHolder.SOLID_FACES);
        if ((solid & (1 << side.ordinal())) != 0) {
            int mask = ~(1 << side.getOpposite().ordinal());
            if (edge != null) {
                mask &= ~(1 << edge.getOpposite().ordinal());
            }
            return (solid & mask) == (mask & FramedCollapsibleCubeBlock.ALL_SOLID);
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        int solid = state.getValue(PropertyHolder.SOLID_FACES);
        return (solid & (1 << edge.ordinal())) != 0;
    }
}
