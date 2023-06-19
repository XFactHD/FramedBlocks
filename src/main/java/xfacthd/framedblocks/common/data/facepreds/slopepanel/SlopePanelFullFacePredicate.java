package xfacthd.framedblocks.common.data.facepreds.slopepanel;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.predicate.FullFacePredicate;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class SlopePanelFullFacePredicate implements FullFacePredicate
{
    public static final SlopePanelFullFacePredicate INSTANCE = new SlopePanelFullFacePredicate();

    private SlopePanelFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side)
    {
        if (state.getValue(PropertyHolder.FRONT))
        {
            return false;
        }
        return side == state.getValue(FramedProperties.FACING_HOR);
    }
}
