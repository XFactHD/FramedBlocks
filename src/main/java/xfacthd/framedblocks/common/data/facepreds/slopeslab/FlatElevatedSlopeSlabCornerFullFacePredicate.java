package xfacthd.framedblocks.common.data.facepreds.slopeslab;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.FullFacePredicate;

public final class FlatElevatedSlopeSlabCornerFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
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
