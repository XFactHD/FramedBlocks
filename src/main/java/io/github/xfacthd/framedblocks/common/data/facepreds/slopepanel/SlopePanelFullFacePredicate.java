package io.github.xfacthd.framedblocks.common.data.facepreds.slopepanel;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class SlopePanelFullFacePredicate implements FullFacePredicate {
    public static final SlopePanelFullFacePredicate INSTANCE = new SlopePanelFullFacePredicate();

    private SlopePanelFullFacePredicate() { }

    @Override
    public boolean test(BlockState state, Direction side) {
        if (state.getValue(PropertyHolder.FRONT)) {
            return false;
        }
        return side == state.getValue(FramedProperties.FACING_HOR);
    }
}
