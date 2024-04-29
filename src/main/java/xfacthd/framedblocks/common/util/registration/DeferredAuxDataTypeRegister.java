package xfacthd.framedblocks.common.util.registration;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.registration.DeferredAuxDataType;

public final class DeferredAuxDataTypeRegister extends DeferredRegister<AuxBlueprintData.Type<?>>
{
    private DeferredAuxDataTypeRegister(String namespace)
    {
        super(FramedConstants.AUX_BLUEPRINT_DATA_TYPE_REGISTRY_KEY, namespace);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <I extends AuxBlueprintData.Type<?>> DeferredHolder<AuxBlueprintData.Type<?>, I> createHolder(
            ResourceKey<? extends Registry<AuxBlueprintData.Type<?>>> registryKey, ResourceLocation key
    )
    {
        return (DeferredHolder) DeferredAuxDataType.createAuxBlueprintDataType(ResourceKey.create(registryKey, key));
    }

    public <T extends AuxBlueprintData<T>> DeferredAuxDataType<T> registerAuxDataType(
            String name, MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec
    )
    {
        //noinspection ConstantConditions
        return (DeferredAuxDataType<T>) register(name, () -> new AuxBlueprintData.Type<>(codec, streamCodec));
    }



    public static DeferredAuxDataTypeRegister create(String namespace)
    {
        return new DeferredAuxDataTypeRegister(namespace);
    }
}
