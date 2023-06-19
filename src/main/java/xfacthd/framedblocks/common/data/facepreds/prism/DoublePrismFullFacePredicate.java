package xfacthd.framedblocks.common.data.facepreds.prism;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.predicate.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class DoublePrismFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction.Axis axis = state.getValue(PropertyHolder.FACING_AXIS).axis();
        return side.getAxis() != axis;
    }
}
