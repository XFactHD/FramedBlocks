package xfacthd.framedblocks.common.data.facepreds.slope;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class DoubleCornerFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);

        if (type.isHorizontal())
        {
            if (side == dir || side == dir.getOpposite())
            {
                return true;
            }
            else if (side == dir.getCounterClockWise())
            {
                return !type.isRight();
            }
            else if (side == dir.getClockWise())
            {
                return type.isRight();
            }
            return (side == Direction.DOWN && !type.isTop()) || (side == Direction.UP && type.isTop());
        }
        else
        {
            return Utils.isY(side) || side == dir || side == dir.getCounterClockWise();
        }
    }
}
