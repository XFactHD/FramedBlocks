package io.github.xfacthd.framedblocks.common.data.facepreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.NullableDirection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class OneWayWindowFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        return face == NullableDirection.NONE || side != face.toDirection();
    }
}
