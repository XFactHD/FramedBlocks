package io.github.xfacthd.framedblocks.common.data.conpreds.pane;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.block.pane.FramedPartialBoardBlock;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.CompoundDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class CornerBoardConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        CompoundDirection cmpDir = state.getValue(PropertyHolder.FACING_DIR);
        Direction face = cmpDir.direction();
        Direction dirOne = cmpDir.orientation();
        Direction dirTwo = FramedPartialBoardBlock.getCornerDirTwo(cmpDir);

        if (side.getAxis() == face.getAxis()) {
            return edge == dirOne || edge == dirTwo;
        }
        if (side.getAxis() == dirOne.getAxis()) {
            return edge == face || edge == dirTwo;
        }
        if (side.getAxis() == dirTwo.getAxis()) {
            return edge == face || edge == dirOne;
        }
        return false;
    }
}
