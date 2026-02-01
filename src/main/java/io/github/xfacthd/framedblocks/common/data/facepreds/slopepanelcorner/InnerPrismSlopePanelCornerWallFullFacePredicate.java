package io.github.xfacthd.framedblocks.common.data.facepreds.slopepanelcorner;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import io.github.xfacthd.framedblocks.common.data.property.HorizontalRotation;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public final class InnerPrismSlopePanelCornerWallFullFacePredicate implements FullFacePredicate
{
    public static final InnerPrismSlopePanelCornerWallFullFacePredicate INSTANCE = new InnerPrismSlopePanelCornerWallFullFacePredicate();

    private InnerPrismSlopePanelCornerWallFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rot = state.getValue(PropertyHolder.ROTATION);
        if (side == dir)
        {
            return state.is(FBContent.BLOCK_FRAMED_SMALL_INNER_PRISM_SLOPE_PANEL_CORNER_WALL);
        }
        return side == rot.withFacing(dir) || side == rot.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);
    }
}
