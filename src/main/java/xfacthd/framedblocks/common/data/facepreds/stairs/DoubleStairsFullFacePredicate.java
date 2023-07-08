package xfacthd.framedblocks.common.data.facepreds.stairs;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class DoubleStairsFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        if (side == state.getValue(FramedProperties.FACING_HOR))
        {
            return true;
        }

        boolean top = state.getValue(FramedProperties.TOP);
        return Utils.isY(side) && top == (side == Direction.UP);
    }
}
