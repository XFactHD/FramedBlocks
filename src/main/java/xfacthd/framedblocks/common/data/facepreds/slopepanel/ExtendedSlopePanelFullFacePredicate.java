package xfacthd.framedblocks.common.data.facepreds.slopepanel;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class ExtendedSlopePanelFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        Direction orientation = state.getValue(PropertyHolder.ROTATION).withFacing(facing);
        return side == facing || side == orientation.getOpposite();
    }
}
