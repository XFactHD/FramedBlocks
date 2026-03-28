package io.github.xfacthd.framedblocks.api.camo.block.rotator;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.Nullable;

final class DefaultBlockCamoRotator implements BlockCamoRotator {
    @Override
    public boolean canRotate(BlockState state) {
        return getRotatableProperty(state) != null;
    }

    @Override
    public @Nullable BlockState rotate(BlockState state) {
        Property<?> prop = getRotatableProperty(state);
        return prop != null ? state.cycle(prop) : null;
    }

    private static @Nullable Property<?> getRotatableProperty(BlockState state) {
        for (Property<?> prop : state.getProperties()) {
            if (prop.getValueClass() == Direction.Axis.class) {
                return prop;
            } else if (prop.getValueClass() == Direction.class) {
                return prop;
            }
        }
        return null;
    }
}
