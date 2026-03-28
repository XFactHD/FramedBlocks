package io.github.xfacthd.framedblocks.common.data.facepreds.prism;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class ElevatedInnerDoubleSlopedPrismFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        return side != state.getValue(PropertyHolder.FACING_DIR).orientation();
    }
}
