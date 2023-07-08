package xfacthd.framedblocks.common.data.facepreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;

public final class FlatElevatedInnerSlopeSlabCornerFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side == dir || side == dir.getCounterClockWise())
        {
            return true;
        }

        if (state.getValue(FramedProperties.TOP))
        {
            return side == Direction.UP;
        }
        else
        {
            return side == Direction.DOWN;
        }
    }
}
