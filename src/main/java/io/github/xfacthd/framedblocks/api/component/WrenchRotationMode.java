package io.github.xfacthd.framedblocks.api.component;

import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Locale;
import java.util.function.IntFunction;

/// Specifies the rotation mode of a Framed Wrench.
public enum WrenchRotationMode implements StringRepresentable {
    /// Primary rotation, usually performs rotation around the Y axis.
    /// Used as fallback value for wrenches from other mods.
    PRIMARY,
    /// Secondary rotation, performs block specific cycling.
    SECONDARY,
    ;

    public static final Codec<WrenchRotationMode> CODEC = StringRepresentable.fromEnum(WrenchRotationMode::values);
    private static final IntFunction<WrenchRotationMode> BY_ID = ByIdMap.continuous(WrenchRotationMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, WrenchRotationMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, WrenchRotationMode::ordinal);

    private final String name = toString().toLowerCase(Locale.ROOT);
    private final Component translatedName = Utils.translate("desc", "framed_wrench.mode." + name);

    /// {@return the translated name of this mode for display in tooltips}
    public Component getTranslatedName() {
        return translatedName;
    }

    /// {@return the next mode after this mode}
    public WrenchRotationMode getNext() {
        return switch (this) {
            case PRIMARY -> SECONDARY;
            case SECONDARY -> PRIMARY;
        };
    }

    /// {@return the default BE notification type of this mode}
    ///
    /// @see IFramedBlock#shouldNotifyBlockEntityOfWrenchRotation(WrenchRotationMode, BlockState, BlockState)
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
