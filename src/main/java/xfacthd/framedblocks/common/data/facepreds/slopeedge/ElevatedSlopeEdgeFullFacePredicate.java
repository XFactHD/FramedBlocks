package xfacthd.framedblocks.common.data.facepreds.slopeedge;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class ElevatedSlopeEdgeFullFacePredicate implements FullFacePredicate
{
    public static final ElevatedSlopeEdgeFullFacePredicate INSTANCE = new ElevatedSlopeEdgeFullFacePredicate();

    private ElevatedSlopeEdgeFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == dir)
        {
            return true;
        }

        return switch (state.getValue(PropertyHolder.SLOPE_TYPE))
        {
            case BOTTOM -> side == Direction.DOWN;
            case HORIZONTAL -> side == dir.getCounterClockWise();
            case TOP -> side == Direction.UP;
        };
    }
}
