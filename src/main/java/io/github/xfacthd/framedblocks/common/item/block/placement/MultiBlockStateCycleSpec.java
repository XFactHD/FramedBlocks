package io.github.xfacthd.framedblocks.common.item.block.placement;

import io.github.xfacthd.framedblocks.common.data.attachment.PlacementStateCycleStorage;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedMap;
import java.util.function.Consumer;

public record MultiBlockStateCycleSpec(List<Block> blocks, SequencedMap<Block, SingleBlockStateCycleSpec> specsByBlock) implements DefaultStateCycleSpec {
    public MultiBlockStateCycleSpec(SequencedMap<Block, SingleBlockStateCycleSpec> specs) {
        this(new ArrayList<>(specs.sequencedKeySet()), specs);
    }

    @Override
    public BlockState getInitialState(@Nullable BlockState placementState) {
        Block specBlock = placementState != null ? placementState.getBlock() : blocks.getFirst();
        return specsByBlock.get(specBlock).getInitialState(placementState);
    }

    @Override
    public BlockState cycle(BlockState state, boolean forward) {
        Block block = state.getBlock();
        SingleBlockStateCycleSpec wrappedSpec = specsByBlock.get(block);
        if (wrappedSpec == null) {
            return state;
        }

        BlockState newState = wrappedSpec.cycle(state, forward);
        if (forward && newState == wrappedSpec.firstState()) {
            newState = getSpec(block, 1).firstState();
        } else if (!forward && newState == wrappedSpec.lastState()) {
            newState = getSpec(block, -1).lastState();
        }
        return newState;
    }

    private SingleBlockStateCycleSpec getSpec(Block prevBlock, int offset) {
        int prevIdx = blocks.indexOf(prevBlock);
        int idx = Mth.positiveModulo(prevIdx + offset, blocks.size());
        return specsByBlock.get(blocks.get(idx));
    }

    @Override
    public @Nullable BlockState postProcessPlacementState(BlockState state, BlockPlaceContext context) {
        return specsByBlock.get(state.getBlock()).postProcessPlacementState(state, context);
    }

    @Override
    public void appendHoverText(Player player, BlockItem item, Consumer<Component> appender) {
        BlockState state = PlacementStateCycleStorage.getSelectedState(player, item);
        if (state != null) {
            specsByBlock.get(state.getBlock()).appendHoverText(state, appender);
        }
    }
}
