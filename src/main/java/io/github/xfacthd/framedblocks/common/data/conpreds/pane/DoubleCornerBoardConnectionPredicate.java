package io.github.xfacthd.framedblocks.common.data.conpreds.pane;

import io.github.xfacthd.framedblocks.api.predicate.contex.NonDetailedConnectionPredicate;
import io.github.xfacthd.framedblocks.common.block.pane.FramedPartialBoardBlock;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class DoubleCornerBoardConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction face = cmpDir.direction();
        Direction dirOne = cmpDir.orientation();
        Direction dirTwo = FramedPartialBoardBlock.getCornerDirTwo(cmpDir);
        if (side == face) {
            return edge == dirOne || edge == dirTwo;
        }
        if (side == dirOne || side == dirTwo) {
            return edge == face;
        }
        return false;
    }
}
