package xfacthd.framedblocks.common.data.facepreds.misc;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;

public final class OneWayWindowFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        return face == NullableDirection.NONE || side != face.toDirection();
    }
}
