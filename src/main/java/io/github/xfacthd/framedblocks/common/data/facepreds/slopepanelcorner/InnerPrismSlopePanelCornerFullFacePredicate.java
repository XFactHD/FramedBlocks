package io.github.xfacthd.framedblocks.common.data.facepreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.FBContent;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class InnerPrismSlopePanelCornerFullFacePredicate implements FullFacePredicate
{
    public static final InnerPrismSlopePanelCornerFullFacePredicate INSTANCE = new InnerPrismSlopePanelCornerFullFacePredicate();

    private InnerPrismSlopePanelCornerFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        Direction downDir = top ? Direction.UP : Direction.DOWN;
        if (side == downDir)
        {
            return state.is(FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER);
        }
        return side == dir || side == dir.getCounterClockWise();
    }
}
