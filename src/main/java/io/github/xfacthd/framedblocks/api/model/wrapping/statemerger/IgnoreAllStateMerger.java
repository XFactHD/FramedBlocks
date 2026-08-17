package io.github.xfacthd.framedblocks.api.model.wrapping.statemerger;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

final class IgnoreAllStateMerger extends StateMerger {
    IgnoreAllStateMerger(Holder<Block> block) {
        super(Set.copyOf(block.value().getStateDefinition().getProperties()));
    }

    @Override
    public BlockState apply(BlockState state) {
        return state.getBlock().defaultBlockState();
    }
}
