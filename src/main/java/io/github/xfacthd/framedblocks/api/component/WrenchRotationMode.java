package io.github.xfacthd.framedblocks.api.component;

import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.TriState;

import java.util.Locale;
import java.util.function.IntFunction;

public enum WrenchRotationMode implements StringRepresentable {
    PRIMARY,
    SECONDARY,
    ;

    public static final Codec<WrenchRotationMode> CODEC = StringRepresentable.fromEnum(WrenchRotationMode::values);
    private static final IntFunction<WrenchRotationMode> BY_ID = ByIdMap.continuous(WrenchRotationMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, WrenchRotationMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, WrenchRotationMode::ordinal);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Component translatedName = Utils.translate("desc", "framed_wrench.mode." + name);

    public Component getTranslatedName() {
        return translatedName;
    }

    public WrenchRotationMode getNext() {
        return switch (this) {
            case PRIMARY -> SECONDARY;
            case SECONDARY -> PRIMARY;
        };
    }

    public TriState getDefaultNotifyBlockEntity() {
        return switch (this) {
            case PRIMARY -> TriState.DEFAULT;
            case SECONDARY -> TriState.FALSE;
        };
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
