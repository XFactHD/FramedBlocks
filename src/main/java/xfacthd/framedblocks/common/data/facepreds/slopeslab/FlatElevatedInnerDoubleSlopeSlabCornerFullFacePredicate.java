package xfacthd.framedblocks.common.data.facepreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class FlatElevatedInnerDoubleSlopeSlabCornerFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        if (Utils.isY(side))
        {
            return true;
        }

        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        return side == facing || side == facing.getCounterClockWise();
    }
}
