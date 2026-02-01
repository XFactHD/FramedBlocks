package io.github.xfacthd.framedblocks.api.util;

import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public enum RotationDirection
{
    CLOCKWISE(Rotation.CLOCKWISE_90),
    COUNTERCLOCKWISE(Rotation.COUNTERCLOCKWISE_90);

    private final Rotation vanillaRotation;

    RotationDirection(Rotation vanillaRotation)
    {
        this.vanillaRotation = vanillaRotation;
    }

    public Rotation toVanillaRotation()
    {
        return vanillaRotation;
    }

    public <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property)
    {
        return switch (this)
        {
            case CLOCKWISE -> state.cycle(property);
            case COUNTERCLOCKWISE ->
            {
                List<T> possibleValues = property.getPossibleValues();
                int currIndex = possibleValues.indexOf(state.getValue(property));
                int prevIndex = Mth.positiveModulo(currIndex - 1, possibleValues.size());
                yield state.setValue(property, possibleValues.get(prevIndex));
            }
        };
    }

    public static RotationDirection of(boolean sneaking)
    {
        return sneaking ? COUNTERCLOCKWISE : CLOCKWISE;
    }
}
