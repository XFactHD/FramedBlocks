package xfacthd.framedblocks.common.data.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import xfacthd.framedblocks.api.blueprint.AuxBlueprintData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedTargetBlockEntity;

public record TargetColor(DyeColor color) implements AuxBlueprintData<TargetColor>
{
    public static final Codec<TargetColor> CODEC = DyeColor.CODEC.xmap(TargetColor::new, TargetColor::color);
    public static final MapCodec<TargetColor> MAP_CODEC = CODEC.fieldOf("color");
    public static final StreamCodec<ByteBuf, TargetColor> STREAM_CODEC = DyeColor.STREAM_CODEC.map(TargetColor::new, TargetColor::color);
    public static final TargetColor DEFAULT = new TargetColor(FramedTargetBlockEntity.DEFAULT_COLOR);

    @Override
    public Type<TargetColor> type()
    {
        return FBContent.AUX_TYPE_TARGET_COLOR.value();
    }
}
