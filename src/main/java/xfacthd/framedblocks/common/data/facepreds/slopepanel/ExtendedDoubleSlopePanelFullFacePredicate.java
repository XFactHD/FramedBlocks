package xfacthd.framedblocks.common.data.facepreds.slopepanel;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class ExtendedDoubleSlopePanelFullFacePredicate implements FullFacePredicate
{
    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (side.getAxis() == dir.getAxis())
        {
            return true;
        }

        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        return side == rot.withFacing(dir).getOpposite();
    }
}
