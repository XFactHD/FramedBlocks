package io.github.xfacthd.framedblocks.common.data.conpreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class ShelfConnectionPredicate implements ConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        Direction backFace = state.getValue(ShelfBlock.FACING).getOpposite();
        return side == backFace || edge == backFace;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction facing = state.getValue(ShelfBlock.FACING);
        return side.getAxis() != facing.getAxis() && edge.getAxis() != facing.getAxis();
    }
}
