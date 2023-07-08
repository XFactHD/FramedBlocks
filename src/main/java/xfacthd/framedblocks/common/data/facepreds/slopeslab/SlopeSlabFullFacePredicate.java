package xfacthd.framedblocks.common.data.facepreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class SlopeSlabFullFacePredicate implements FullFacePredicate
{
    public static final SlopeSlabFullFacePredicate INSTANCE = new SlopeSlabFullFacePredicate();

    private SlopeSlabFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side)
    {
        boolean topHalf = state.getValue(PropertyHolder.TOP_HALF);
        if (state.getValue(FramedProperties.TOP))
        {
            return topHalf && side == Direction.UP;
        }
        else
        {
            return !topHalf && side == Direction.DOWN;
        }
    }
}
