package xfacthd.framedblocks.api.blueprint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import xfacthd.framedblocks.api.FramedBlocksAPI;

public interface AuxBlueprintData<T extends AuxBlueprintData<T>>
{
    Registry<Type<?>> REGISTRY = FramedBlocksAPI.INSTANCE.getAuxBlueprintDataTypeRegistry();
    Codec<AuxBlueprintData<?>> CODEC = REGISTRY.byNameCodec().dispatch(AuxBlueprintData::type, Type::codec);
    StreamCodec<RegistryFriendlyByteBuf, AuxBlueprintData<?>> STREAM_CODEC = ByteBufCodecs.registry(REGISTRY.key())
            .dispatch(AuxBlueprintData::type, Type::streamCodec);

    Type<T> type();

    int hashCode();

    boolean equals(Object other);



    record Type<T extends AuxBlueprintData<?>>(MapCodec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) { }
}
