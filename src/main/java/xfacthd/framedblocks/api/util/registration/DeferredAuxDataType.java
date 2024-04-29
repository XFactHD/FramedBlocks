package xfacthd.framedblocks.api.util.registration;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.api.util.FramedConstants;

public final class DeferredAuxDataType<T extends AuxBlueprintData<T>> extends DeferredHolder<AuxBlueprintData.Type<?>, AuxBlueprintData.Type<T>>
{
    private DeferredAuxDataType(ResourceKey<AuxBlueprintData.Type<?>> key)
    {
        super(key);
    }



    public static <T extends AuxBlueprintData<T>> DeferredAuxDataType<T> createAuxBlueprintDataType(ResourceLocation name)
    {
        return createAuxBlueprintDataType(ResourceKey.create(FramedConstants.AUX_BLUEPRINT_DATA_TYPE_REGISTRY_KEY, name));
    }

    public static <T extends AuxBlueprintData<T>> DeferredAuxDataType<T> createAuxBlueprintDataType(ResourceKey<AuxBlueprintData.Type<?>> key)
    {
        return new DeferredAuxDataType<>(key);
    }
}
