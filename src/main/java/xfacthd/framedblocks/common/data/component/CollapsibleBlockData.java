package xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.property.NullableDirection;

public record CollapsibleBlockData(NullableDirection collapsedFace, int offsets) implements AuxBlueprintData<CollapsibleBlockData>
{
    public static final Codec<CollapsibleBlockData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            NullableDirection.CODEC.fieldOf("collapsed_face").forGetter(CollapsibleBlockData::collapsedFace),
            Codec.INT.fieldOf("offsets").forGetter(CollapsibleBlockData::offsets)
    ).apply(inst, CollapsibleBlockData::new));
    public static final MapCodec<CollapsibleBlockData> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            NullableDirection.CODEC.fieldOf("collapsed_face").forGetter(CollapsibleBlockData::collapsedFace),
            Codec.INT.fieldOf("offsets").forGetter(CollapsibleBlockData::offsets)
    ).apply(inst, CollapsibleBlockData::new));
    public static final StreamCodec<FriendlyByteBuf, CollapsibleBlockData> STREAM_CODEC = StreamCodec.composite(
            NullableDirection.STREAM_CODEC,
            CollapsibleBlockData::collapsedFace,
            ByteBufCodecs.VAR_INT,
            CollapsibleBlockData::offsets,
            CollapsibleBlockData::new
    );
    public static final CollapsibleBlockData EMPTY = new CollapsibleBlockData(NullableDirection.NONE, 0);

    public CollapsibleBlockData(@Nullable Direction collapsedFace, int offsets)
    {
        this(NullableDirection.fromDirection(collapsedFace), offsets);
    }

    @Override
    public Type<CollapsibleBlockData> type()
    {
        return FBContent.AUX_TYPE_COLLAPSIBLE_BLOCK_DATA.value();
    }
}
