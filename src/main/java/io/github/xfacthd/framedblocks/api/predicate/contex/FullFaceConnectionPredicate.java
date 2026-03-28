package io.github.xfacthd.framedblocks.api.predicate.contex;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

final class FullFaceConnectionPredicate extends NonDetailedConnectionPredicate {
    @Override
    public boolean canConnectFullEdge(BlockState state, Direction side, @Nullable Direction edge) {
        // Cannot be replaced with cache access because it's used within cache init
        return ((IFramedBlock) state.getBlock()).getBlockType().getFullFacePredicate().test(state, side);
    }
}
