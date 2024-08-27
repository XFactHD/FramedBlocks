package xfacthd.framedblocks.common.data.facepreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class InnerCornerFullFacePredicate implements FullFacePredicate
{
    public static final InnerCornerFullFacePredicate INSTANCE = new InnerCornerFullFacePredicate();

    private InnerCornerFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if ((type == CornerType.TOP || (type.isHorizontal() && type.isTop())) && side == Direction.UP)
        {
            return true;
        }
        else if ((type == CornerType.BOTTOM || (type.isHorizontal() && !type.isTop())) && side == Direction.DOWN)
        {
            return true;
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (type.isHorizontal())
        {
            return facing == side || (type.isRight() && facing.getClockWise() == side) || (!type.isRight() && facing.getCounterClockWise() == side);
        }
        else
        {
            return facing == side || facing.getCounterClockWise() == side;
        }
    }
}
