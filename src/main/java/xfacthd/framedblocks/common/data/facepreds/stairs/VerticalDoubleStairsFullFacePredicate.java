package xfacthd.framedblocks.common.data.facepreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.StairsType;

public final class VerticalDoubleStairsFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        StairsType type = state.getValue(PropertyHolder.STAIRS_TYPE);

        if (side == facing)
        {
            return type == StairsType.VERTICAL || type == StairsType.TOP_CCW || type == StairsType.BOTTOM_CCW;
        }
        if (side == facing.getCounterClockWise())
        {
            return type == StairsType.VERTICAL || type == StairsType.TOP_FWD || type == StairsType.BOTTOM_FWD;
        }
        return false;
    }
}
