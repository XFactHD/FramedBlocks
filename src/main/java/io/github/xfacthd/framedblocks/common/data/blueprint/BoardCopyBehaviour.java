package io.github.xfacthd.framedblocks.common.data.blueprint;

import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import io.github.xfacthd.framedblocks.common.data.PropertyHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Set;

public final class BoardCopyBehaviour implements BlueprintCopyBehaviour {
    @Override
    public ItemStack getBlockItem(BlueprintData data) {
        ItemStack stack = BlueprintCopyBehaviour.super.getBlockItem(data);
        Integer faces = data.blockState().get(PropertyHolder.FACES);
        if (faces != null) {
            stack.setCount(Integer.bitCount(faces));
        }
        return stack;
    }

    @Override
    public Set<Property<?>> getPropertiesToCopy(BlockState state) {
        if (Integer.bitCount(state.getValue(PropertyHolder.FACES)) > 1) {
            return Set.of(PropertyHolder.FACES);
        }
        return Set.of();
    }
}
