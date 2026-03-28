package io.github.xfacthd.framedblocks.common.data.facepreds.prism;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.DirectionAxis;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class ElevatedInnerPrismFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        DirectionAxis dirAxis = state.getValue(PropertyHolder.FACING_AXIS);
        return side != dirAxis.direction() && side.getAxis() != dirAxis.axis();
    }
}
