package xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.common.FBContent;

public record AdjustableDoubleBlockData(int firstHeight) implements AuxBlueprintData<AdjustableDoubleBlockData>
{
    public static final Codec<AdjustableDoubleBlockData> CODEC = Codec.INT
            .xmap(AdjustableDoubleBlockData::new, AdjustableDoubleBlockData::firstHeight);
    public static final MapCodec<AdjustableDoubleBlockData> MAP_CODEC = CODEC.fieldOf("first_height");
    public static final StreamCodec<ByteBuf, AdjustableDoubleBlockData> STREAM_CODEC = ByteBufCodecs.VAR_INT
            .map(AdjustableDoubleBlockData::new, AdjustableDoubleBlockData::firstHeight);
    public static final AdjustableDoubleBlockData EMPTY = new AdjustableDoubleBlockData(0);

    @Override
    public Type<AdjustableDoubleBlockData> type()
    {
        return FBContent.AUX_TYPE_ADJ_DOUBLE_BLOCK_DATA.value();
    }
}
