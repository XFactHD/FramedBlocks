package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import xfacthd.framedblocks.api.blueprint.*;

public class DummyDataHandlingCopyBehaviour<T extends AuxBlueprintData<T>> implements BlueprintCopyBehaviour
{
    private final DataComponentType<T> componentType;
    private final T auxDefaultValue;

    public DummyDataHandlingCopyBehaviour(DataComponentType<T> componentType, T auxDefaultValue)
    {
        this.componentType = componentType;
        this.auxDefaultValue = auxDefaultValue;
    }

    @Override
    public final void attachDataToDummyRenderStack(ItemStack stack, BlueprintData data)
    {
        T toCopy = data.getAuxDataOrDefault(auxDefaultValue);
        stack.set(componentType, toCopy);
    }
}
