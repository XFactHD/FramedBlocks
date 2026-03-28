package io.github.xfacthd.framedblocks.common.data.conpreds.pillar;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.contex.ConnectionPredicate;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public final class LatticeConnectionPredicate implements ConnectionPredicate {
    public static final LatticeConnectionPredicate INSTANCE = new LatticeConnectionPredicate();

    private LatticeConnectionPredicate() { }

    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        return false;
    }

    @Override
    public boolean canConnectDetailed(BlockState state, Direction side, Direction edge) {
        boolean x = state.getValue(FramedProperties.X_AXIS);
        boolean y = state.getValue(FramedProperties.Y_AXIS);
        boolean z = state.getValue(FramedProperties.Z_AXIS);
        return switch (side.getAxis()) {
            case X -> (y && DirUtils.isY(edge)) || (z && DirUtils.isZ(edge));
            case Y -> (x && DirUtils.isX(edge)) || (z && DirUtils.isZ(edge));
            case Z -> (x && DirUtils.isX(edge)) || (y && DirUtils.isY(edge));
        };
    }
}
