package io.github.xfacthd.framedblocks.api.util;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

/// Represents the rotation direction of a wrench.
public enum RotationDirection {
    /// Rotate clockwise (forward).
    CLOCKWISE(Rotation.CLOCKWISE_90),
    /// Rotate counterclockwise (backward).
    COUNTERCLOCKWISE(Rotation.COUNTERCLOCKWISE_90);

    private final Rotation vanillaRotation;

    RotationDirection(Rotation vanillaRotation) {
        this.vanillaRotation = vanillaRotation;
    }

    /// {@return the vanilla rotation represented by this rotation direction}
    public Rotation toVanillaRotation() {
        return vanillaRotation;
    }

    /// {@return the opposite of this rotation direction}
    public RotationDirection getOpposite() {
        return switch (this) {
            case CLOCKWISE -> COUNTERCLOCKWISE;
            case COUNTERCLOCKWISE -> CLOCKWISE;
        };
    }

    /// Cycle the given property on the given state in the direction indicated by this rotation direction.
    ///
    /// @param state    The state to cycle the property on
    /// @param property The property to cycle
    public <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property) {
        return switch (this) {
            case CLOCKWISE -> state.cycle(property);
            case COUNTERCLOCKWISE -> {
                List<T> possibleValues = property.getPossibleValues();
                int currIndex = possibleValues.indexOf(state.getValue(property));
                int prevIndex = Mth.positiveModulo(currIndex - 1, possibleValues.size());
                yield state.setValue(property, possibleValues.get(prevIndex));
            }
        };
    }

    /// Rotate the [BlockStateProperties#ROTATION_16] property on the given state by one in the
    /// direction indicated by this rotation direction.
    ///
    /// @param state The state to cycle the property on
    public BlockState rotateRot16(BlockState state) {
        int offset = switch (this) {
            case CLOCKWISE -> 1;
            case COUNTERCLOCKWISE -> 15;
        };
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        return state.setValue(BlockStateProperties.ROTATION_16, (rotation + offset) % 16);
    }

    /// {@return the rotation direction for the player's crouch state}
    ///
    /// @param sneaking Whether the player is crouching
    public static RotationDirection of(boolean sneaking) {
        return sneaking ? COUNTERCLOCKWISE : CLOCKWISE;
    }
}
