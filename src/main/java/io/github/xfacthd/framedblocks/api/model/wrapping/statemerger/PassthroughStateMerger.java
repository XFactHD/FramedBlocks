package io.github.xfacthd.framedblocks.api.model.wrapping.statemerger;

import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

final class PassthroughStateMerger extends StateMerger {
    PassthroughStateMerger() {
        super(Set.of());
    }

    @Override
    public BlockState apply(BlockState state) {
        return state;
    }
}
