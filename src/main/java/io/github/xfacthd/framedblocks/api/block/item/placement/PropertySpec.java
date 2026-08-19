package io.github.xfacthd.framedblocks.api.block.item.placement;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Set;

@ApiStatus.Internal
public record PropertySpec<T extends Comparable<T>>(Property<T> property, List<T> values, Set<T> valueSet) {
    public PropertySpec(Property<T> property, List<T> values) {
        this(property, values, Set.copyOf(values));
    }

    public BlockState setInitial(BlockState state) {
        return state.setValue(property, values.getFirst());
    }

    public BlockState copyValue(BlockState srcState, BlockState targetState) {
        return targetState.setValue(property, srcState.getValue(property));
    }

    public BlockState cycle(BlockState state) {
        T value = state.getValue(property);
        int idx = values.indexOf(value);
        int newIdx = (idx + 1) % values.size();
        return state.setValue(property, values.get(newIdx));
    }

    public boolean isValidValue(BlockState state) {
        return valueSet.contains(state.getValue(property));
    }

    public boolean isSameValue(BlockState stateOne, BlockState stateTwo) {
        return stateOne.getValue(property).equals(stateTwo.getValue(property));
    }
}
