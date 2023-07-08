package xfacthd.framedblocks.common.data.facepreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class VerticalStairsFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);
        if (type == StairsType.VERTICAL)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return side == dir || side == dir.getCounterClockWise();
        }
        return false;
    }
}
