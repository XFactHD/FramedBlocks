package io.github.xfacthd.framedblocks.common.data.conpreds.slope;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.common.block.SlopeBlock;
import io.github.xfacthd.framedblocks.common.data.property.SlopeType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class SlopeConnectionPredicate implements ConnectionPredicate {
    public static final SlopeConnectionPredicate INSTANCE = new SlopeConnectionPredicate();

    private SlopeConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        SlopeBlock block = (SlopeBlock) state.getBlock();
        SlopeType type = block.getSlopeType(state);
        if (type == SlopeType.HORIZONTAL) {
            Direction dirOne = state.getValue(FramedProperties.FACING_HOR);
            Direction dirTwo = dirOne.getCounterClockWise();
            if (side == dirOne || side == dirTwo) {
                return true;
            }
            if (DirUtils.isY(side)) {
                return edge == dirOne || edge == dirTwo;
            }
            return false;
        }

        Direction dirOne = block.getFacing(state);
        Direction dirTwo = type == SlopeType.TOP ? Direction.UP : Direction.DOWN;
        if (side == dirOne || side == dirTwo) {
            return true;
        }
        if (side.getAxis() == dirOne.getClockWise().getAxis()) {
            return edge == dirOne || edge == dirTwo;
        }
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        SlopeBlock block = (SlopeBlock) state.getBlock();
        SlopeType type = block.getSlopeType(state);
        if (type == SlopeType.HORIZONTAL) {
            Direction dirOne = state.getValue(FramedProperties.FACING_HOR).getOpposite();
            Direction dirTwo = dirOne.getCounterClockWise();

            if (side == dirOne || side == dirTwo) {
                return DirUtils.isY(edge);
            }
            return false;
        }

        Direction dirOne = block.getFacing(state).getOpposite();
        Direction dirTwo = type == SlopeType.TOP ? Direction.DOWN : Direction.UP;
        if (side == dirOne || side == dirTwo) {
            return edge.getAxis() == dirOne.getClockWise().getAxis();
        }
        return false;
    }
}
