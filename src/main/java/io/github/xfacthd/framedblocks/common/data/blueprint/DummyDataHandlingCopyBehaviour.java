package io.github.xfacthd.framedblocks.common.data.blueprint;

import io.github.xfacthd.framedblocks.api.blueprint.BlueprintCopyBehaviour;
import io.github.xfacthd.framedblocks.api.blueprint.BlueprintData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public class DummyDataHandlingCopyBehaviour<T> implements BlueprintCopyBehaviour {
    private final DataComponentType<T> componentType;
    private final T auxDefaultValue;

    public DummyDataHandlingCopyBehaviour(DataComponentType<T> componentType, T auxDefaultValue) {
        this.componentType = componentType;
        this.auxDefaultValue = auxDefaultValue;
    }

    @Override
    public final void attachDataToDummyRenderStack(ItemStack stack, BlueprintData data) {
        T toCopy = data.getCustomDataOrDefault(componentType, auxDefaultValue);
        stack.set(componentType, toCopy);
    }
}
