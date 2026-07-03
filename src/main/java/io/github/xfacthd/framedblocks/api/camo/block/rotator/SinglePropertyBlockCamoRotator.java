package io.github.xfacthd.framedblocks.api.camo.block.rotator;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/// Camo rotator capable of cycling through one property of a given block.
public final class SinglePropertyBlockCamoRotator implements BlockCamoRotator {
    private final Property<?> property;

    public SinglePropertyBlockCamoRotator(Property<?> property) {
        this.property = property;
    }

    @Override
    public boolean canRotate(BlockState state) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState state) {
        return state.cycle(property);
    }
}
