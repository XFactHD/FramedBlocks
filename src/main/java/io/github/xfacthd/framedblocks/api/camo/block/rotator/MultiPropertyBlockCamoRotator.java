package io.github.xfacthd.framedblocks.api.camo.block.rotator;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

public final class MultiPropertyBlockCamoRotator implements BlockCamoRotator {
    private final BlockState defaultState;
    private final Property<?>[] properties;

    public MultiPropertyBlockCamoRotator(Block block, List<Property<?>> properties) {
        this.defaultState = block.defaultBlockState();
        this.properties = properties.toArray(Property[]::new);
    }

    @Override
    public boolean canRotate(BlockState state) {
        return true;
    }

    @Override
    public BlockState rotate(BlockState state) {
        for (Property<?> property : properties) {
            state = state.cycle(property);
            if (!state.getValue(property).equals(defaultState.getValue(property))) {
                break;
            }
        }
        return state;
    }
}
