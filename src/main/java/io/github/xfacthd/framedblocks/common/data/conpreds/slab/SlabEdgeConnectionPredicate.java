package io.github.xfacthd.framedblocks.common.data.conpreds.slab;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SlabEdgeConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction dirOne = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.UP : Direction.DOWN;
        return (side == dirOne && edge == dirTwo) || (side == dirTwo && edge == dirOne);
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction dirOne = state.getValue(FramedProperties.FACING_HOR);
        Direction dirTwo = state.getValue(FramedProperties.TOP) ? Direction.DOWN : Direction.UP;
        if (side == dirTwo || side == dirTwo.getOpposite()) {
            return edge != dirOne.getOpposite();
        }
        if (side == dirOne || side == dirOne.getOpposite()) {
            return edge != dirTwo;
        }
        if (side.getAxis() != dirOne.getAxis()) {
            return edge == dirOne || edge == dirTwo.getOpposite();
        }
        return false;
    }
}
