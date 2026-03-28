package io.github.xfacthd.framedblocks.common.data.conpreds.pane;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.block.pane.FramedBoardBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class BoardConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        if (FramedBoardBlock.isFacePresent(state, side)) {
            return true;
        }
        return edge != null && FramedBoardBlock.isFacePresent(state, edge);
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        if (FramedBoardBlock.isFacePresent(state, side)) {
            return false;
        }
        return !FramedBoardBlock.isFacePresent(state, edge);
    }
}
