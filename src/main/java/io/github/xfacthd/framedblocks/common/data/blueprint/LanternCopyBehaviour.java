package io.github.xfacthd.framedblocks.common.data.blueprint;

import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

public final class LanternCopyBehaviour implements BlueprintCopyBehaviour {
    @Override
    public Set<Property<?>> getPropertiesToCopy(BlockState state) {
        return Utils.concat(BlueprintCopyBehaviour.super.getPropertiesToCopy(state), Set.of(PropertyHolder.CHAIN_TYPE));
    }
}
