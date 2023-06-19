package xfacthd.framedblocks.common.data.facepreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.FullFacePredicate;

public final class InnerThreewayCornerFullFacePredicate implements FullFacePredicate
{
    public static final InnerThreewayCornerFullFacePredicate INSTANCE = new InnerThreewayCornerFullFacePredicate();

    private InnerThreewayCornerFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side)
    {
        boolean top = state.getValue(FramedProperties.TOP);
        if (top && side == Direction.UP)
        {
            return true;
        }
        else if (!top && side == Direction.DOWN)
        {
            return true;
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        return facing == side || facing.getCounterClockWise() == side;
    }
}
