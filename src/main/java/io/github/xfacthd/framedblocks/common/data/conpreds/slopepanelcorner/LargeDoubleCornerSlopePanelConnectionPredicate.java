package io.github.xfacthd.framedblocks.common.data.conpreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class LargeDoubleCornerSlopePanelConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);

        if (side == dir.getOpposite() || side == dir.getClockWise()) {
            return true;
        }
        if (DirUtils.isY(side)) {
            return edge == dir.getOpposite() || edge == dir.getClockWise();
        }
        if (side == dir) {
            return edge == dir.getClockWise();
        }
        if (side == dir.getCounterClockWise()) {
            return edge == dir.getOpposite();
        }
        return false;
    }
}
