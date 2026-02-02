package xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.common.FBContent;

public record CollapsibleBlockData(int offsets) implements AuxBlueprintData<CollapsibleBlockData>
{
    public static final Codec<CollapsibleBlockData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("offsets").forGetter(CollapsibleBlockData::offsets)
    ).apply(inst, CollapsibleBlockData::new));
    public static final MapCodec<CollapsibleBlockData> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.INT.fieldOf("offsets").forGetter(CollapsibleBlockData::offsets)
    ).apply(inst, CollapsibleBlockData::new));
    public static final StreamCodec<FriendlyByteBuf, CollapsibleBlockData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            CollapsibleBlockData::offsets,
            CollapsibleBlockData::new
    );
    public static final CollapsibleBlockData EMPTY = new CollapsibleBlockData(0);

    @Override
    public Type<CollapsibleBlockData> type()
    {
        return FBContent.AUX_TYPE_COLLAPSIBLE_BLOCK_DATA.value();
    }
}
