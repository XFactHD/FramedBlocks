package io.github.xfacthd.framedblocks.common.data.conpreds.door;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import org.jspecify.annotations.Nullable;

public final class TrapdoorConnectionPredicate implements ConnectionPredicate {
    public static final TrapdoorConnectionPredicate INSTANCE = new TrapdoorConnectionPredicate();

    private TrapdoorConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        boolean open = state.getValue(BlockStateProperties.OPEN);
        if (open && state.getValue(PropertyHolder.ROTATE_TEXTURE)) {
            return false;
        }

        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

        Direction fullFace = facing.getOpposite();
        if (!open) {
            Half half = state.getValue(BlockStateProperties.HALF);
            fullFace = half == Half.BOTTOM ? Direction.DOWN : Direction.UP;
        }

        if (side == fullFace) {
            return true;
        } else if (side.getAxis() != fullFace.getAxis()) {
            return edge == fullFace;
        }

        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        boolean open = state.getValue(BlockStateProperties.OPEN);
        if (open && state.getValue(PropertyHolder.ROTATE_TEXTURE)) {
            return false;
        }

        Direction fullFace = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (!open) {
            Half half = state.getValue(BlockStateProperties.HALF);
            fullFace = half == Half.BOTTOM ? Direction.UP : Direction.DOWN;
        }

        if (side == fullFace) {
            return true;
        } else if (side.getAxis() != fullFace.getAxis()) {
            return edge == fullFace.getOpposite() || edge.getAxis() != fullFace.getAxis();
        }

        return false;
    }
}
