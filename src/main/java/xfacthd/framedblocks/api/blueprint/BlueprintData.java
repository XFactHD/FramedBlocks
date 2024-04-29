package xfacthd.framedblocks.api.blueprint;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import xfacthd.framedblocks.api.util.CamoList;

import java.util.Optional;

public record BlueprintData(
        Block block,
        CamoList camos,
        boolean glowing,
        boolean intangible,
        boolean reinforced,
        Optional<AuxBlueprintData<?>> auxData
)
{
    public static final Codec<BlueprintData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlueprintData::block),
            CamoList.CODEC.fieldOf("camos").forGetter(BlueprintData::camos),
            Codec.BOOL.fieldOf("glowing").forGetter(BlueprintData::glowing),
            Codec.BOOL.fieldOf("intangible").forGetter(BlueprintData::intangible),
            Codec.BOOL.fieldOf("reinforced").forGetter(BlueprintData::reinforced),
            AuxBlueprintData.CODEC.optionalFieldOf("aux_data").forGetter(BlueprintData::auxData)
    ).apply(inst, BlueprintData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, BlueprintData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.BLOCK),
            BlueprintData::block,
            CamoList.STREAM_CODEC,
            BlueprintData::camos,
            ByteBufCodecs.BOOL,
            BlueprintData::glowing,
            ByteBufCodecs.BOOL,
            BlueprintData::intangible,
            ByteBufCodecs.BOOL,
            BlueprintData::reinforced,
            ByteBufCodecs.optional(AuxBlueprintData.STREAM_CODEC),
            BlueprintData::auxData,
            BlueprintData::new
    );
    public static final BlueprintData EMPTY = new BlueprintData(Blocks.AIR, CamoList.EMPTY, false, false, false, Optional.empty());

    @SuppressWarnings("unchecked")
    public <T extends AuxBlueprintData<T>> T getAuxDataOrDefault(T _default)
    {
        if (auxData.isPresent() && _default.type() == auxData.get().type())
        {
            return (T) auxData.get();
        }
        return _default;
    }

    public boolean isEmpty()
    {
        return block.defaultBlockState().isAir();
    }

    public BlueprintData withAuxData(AuxBlueprintData<?> newAuxData)
    {
        return new BlueprintData(block, camos, glowing, intangible, reinforced, Optional.of(newAuxData));
    }
}
