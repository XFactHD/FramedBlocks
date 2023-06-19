package xfacthd.framedblocks.common.data.facepreds.slopepanel;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class FlatDoubleSlopePanelCornerFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        if (state.getValue(PropertyHolder.FRONT))
        {
            return side == facing.getOpposite();
        }
        else
        {
            return side == facing;
        }
    }
}
