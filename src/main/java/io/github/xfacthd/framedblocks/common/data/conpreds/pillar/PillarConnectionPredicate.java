package io.github.xfacthd.framedblocks.common.data.conpreds.pillar;

import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;

public final class PillarConnectionPredicate implements ConnectionPredicate {
    public static final PillarConnectionPredicate INSTANCE = new PillarConnectionPredicate();

    private PillarConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        return side.getAxis() != axis && edge.getAxis() == axis;
    }
}
