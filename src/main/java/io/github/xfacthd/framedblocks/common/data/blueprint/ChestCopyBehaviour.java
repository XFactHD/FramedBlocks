package io.github.xfacthd.framedblocks.common.data.blueprint;

import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

public final class ChestCopyBehaviour implements BlueprintCopyBehaviour {
    @Override
    public Set<Property<?>> getPropertiesToCopy(BlockState state) {
        return Set.of(PropertyHolder.LATCH_TYPE);
    }
}
