package xfacthd.framedblocks.common.data.facepreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class VerticalSlicedStairsFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        boolean right = state.getValue(PropertyHolder.RIGHT);

        if (right)
        {
            return side == dir.getCounterClockWise() && !type.isCounterClockwise();
        }
        else
        {
            return side == dir && !type.isForward();
        }
    }
}
