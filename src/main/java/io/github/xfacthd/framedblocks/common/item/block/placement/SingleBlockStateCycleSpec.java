package io.github.xfacthd.framedblocks.common.item.block.placement;

import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyPrinter;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertySpec;
import io.github.xfacthd.framedblocks.api.block.item.placement.PlacementStatePostProcessor;
import io.github.xfacthd.framedblocks.api.util.text.MoreCommonComponents;
import io.github.xfacthd.framedblocks.common.data.attachment.PlacementStateCycleStorage;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SequencedMap;
import java.util.function.Consumer;

public record SingleBlockStateCycleSpec(
        List<PropertySpec<?>> properties,
        List<BlockState> stateByIdx,
        Reference2IntMap<BlockState> idxByState,
        List<PrintableProperty<?>> printers,
        @Nullable PlacementStatePostProcessor postProcessor,
        BlockState firstState,
        BlockState lastState
) implements DefaultStateCycleSpec {
    public SingleBlockStateCycleSpec(
            Block block,
            List<PropertySpec<?>> properties,
            SequencedMap<Property<?>, PropertyPrinter<?>> printerMap,
            @Nullable PlacementStatePostProcessor postProcessor,
            boolean lockState
    ) {
        BlockState firstState = block.defaultBlockState();
        for (PropertySpec<?> propSpec : properties) {
            firstState = propSpec.setInitial(firstState);
        }
        if (lockState) {
            firstState = firstState.setValue(FramedProperties.STATE_LOCKED, true);
        }

        int expectedCount = 1;
        for (PropertySpec<?> property : properties) {
            expectedCount *= property.values().size();
        }
        List<BlockState> stateByIdx = new ArrayList<>(expectedCount);
        BlockState state = firstState;
        int pass = 0;
        do {
            stateByIdx.add(state);
            for (PropertySpec<?> propSpec : properties) {
                state = propSpec.cycle(state);
                if (!propSpec.isSameValue(state, firstState)) {
                    break;
                }
            }
            pass++;
            if (pass > expectedCount) {
                throw new IllegalStateException(String.format(Locale.ROOT, "State sequence computation for %s exceeded expected cycle count %s", block, expectedCount));
            }
        } while (state != firstState);

        Reference2IntMap<BlockState> idxByState = new Reference2IntOpenHashMap<>(stateByIdx.size());
        for (int i = 0; i < stateByIdx.size(); i++) {
            idxByState.put(stateByIdx.get(i), i);
        }

        List<PrintableProperty<?>> printers = printerMap.entrySet()
                .stream()
                .<PrintableProperty<?>>map(PrintableProperty::of)
                .toList();

        this(properties, stateByIdx, idxByState, printers, postProcessor, stateByIdx.getFirst(), stateByIdx.getLast());
    }

    @Override
    public BlockState getInitialState(@Nullable BlockState placementState) {
        BlockState initialState = firstState;
        if (placementState != null) {
            for (PropertySpec<?> property : properties) {
                initialState = property.copyValue(placementState, initialState);
            }
        }
        return initialState;
    }

    @Override
    public BlockState cycle(BlockState state, boolean forward) {
        int idx = idxByState.getInt(state);
        if (idx == -1) {
            return state;
        }

        int offset = forward ? 1 : -1;
        int newIdx = Mth.positiveModulo(idx + offset, stateByIdx.size());
        return stateByIdx.get(newIdx);
    }

    @Override
    public @Nullable BlockState postProcessPlacementState(BlockState state, BlockPlaceContext context) {
        if (postProcessor != null) {
            state = postProcessor.postProcessPlacementState(state, context);
        }
        return state;
    }

    @Override
    public void appendHoverText(Player player, BlockItem item, Consumer<Component> appender) {
        BlockState state = PlacementStateCycleStorage.getSelectedState(player, item);
        if (state != null) {
            appendHoverText(state, appender);
        }
    }

    void appendHoverText(BlockState state, Consumer<Component> appender) {
        PropertyPrinter.LineOutput output = (label, value) -> {
            Component line = Component.translatable(label, value).withStyle(ChatFormatting.GOLD);
            appender.accept(MoreCommonComponents.indent(line));
        };
        for (PrintableProperty<?> property : printers) {
            property.print(state, output);
        }
    }

    public record PrintableProperty<T extends Comparable<T>>(Property<T> property, PropertyPrinter<T> printer) {
        @SuppressWarnings("unchecked")
        static <T extends Comparable<T>> PrintableProperty<T> of(Map.Entry<Property<?>, PropertyPrinter<?>> entry) {
            return new PrintableProperty<>((Property<T>) entry.getKey(), (PropertyPrinter<T>) entry.getValue());
        }

        void print(BlockState state, PropertyPrinter.LineOutput output) {
            printer.print(state.getValue(property), output);
        }
    }
}
