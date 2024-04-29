package xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.saveddata.maps.MapFrame;
import xfacthd.framedblocks.api.util.FramedConstants;

public record FramedMap(BlockPos pos, int yRot)
{
    public static final Codec<FramedMap> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(FramedMap::pos),
            Codec.INT.fieldOf("rot").forGetter(FramedMap::yRot)
    ).apply(inst, FramedMap::new));
    public static final StreamCodec<FriendlyByteBuf, FramedMap> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            FramedMap::pos,
            ByteBufCodecs.VAR_INT,
            FramedMap::yRot,
            FramedMap::new
    );

    public FramedMap(BlockPos pos, Direction dir)
    {
        this(pos, dir.get2DDataValue() * 90);
    }



    public static String makeFrameId(BlockPos pos)
    {
        return FramedConstants.MOD_ID + ":" + MapFrame.frameId(pos);
    }



    public interface MarkerRemover
    {
        void framedblocks$removeMapMarker(BlockPos pos);
    }
}
