package io.github.xfacthd.framedblocks.common.data.facepreds.misc;

import io.github.xfacthd.framedblocks.api.predicate.fullface.FullFacePredicate;
import io.github.xfacthd.framedblocks.common.block.pane.FramedBoardBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public final class BoardFullFacePredicate implements FullFacePredicate {
    @Override
    public boolean test(BlockState state, Direction side) {
        return FramedBoardBlock.isFacePresent(state, side);
    }
}
