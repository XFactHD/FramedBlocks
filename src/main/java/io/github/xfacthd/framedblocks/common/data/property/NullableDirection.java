package io.github.xfacthd.framedblocks.common.data.property;

import com.mojang.serialization.Codec;
import io.github.xfacthd.framedblocks.api.block.item.placement.ValueOrders;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.text.MoreCommonComponents;
import io.github.xfacthd.framedblocks.api.util.text.Printable;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.IntFunction;

public enum NullableDirection implements StringRepresentable, Printable {
    NONE(null),
    DOWN(Direction.DOWN),
    UP(Direction.UP),
    NORTH(Direction.NORTH),
    SOUTH(Direction.SOUTH),
    WEST(Direction.WEST),
    EAST(Direction.EAST);

    private static final IntFunction<NullableDirection> BY_ID = ByIdMap.continuous(NullableDirection::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final Codec<NullableDirection> CODEC = StringRepresentable.fromEnum(NullableDirection::values);
    public static final StreamCodec<ByteBuf, NullableDirection> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, NullableDirection::ordinal);
    public static final List<NullableDirection> CYCLE_ORDER = Util.make(() -> {
        List<NullableDirection> values = new ArrayList<>(List.of(values()));
        values.sort(Comparator.comparingInt(dir -> dir == NONE ? 0 : ValueOrders.FACING.indexOf(dir.toDirection()) + 1));
        return List.copyOf(values);
    });
    public static final Component VALUE_NONE = Utils.translate("value", "nullable_direction.none");

    @Nullable
    private final Direction dir;
    private final String name = toString().toLowerCase(Locale.ROOT);

    NullableDirection(@Nullable Direction dir) {
        this.dir = dir;
    }

    public Direction toDirection() {
        return Objects.requireNonNull(dir);
    }

    public @Nullable Direction toNullableDirection() {
        return dir;
    }

    public NullableDirection rotate(Rotation rotation) {
        return dir != null ? fromDirection(rotation.rotate(dir)) : this;
    }

    public NullableDirection mirror(Mirror mirror) {
        return dir != null ? fromDirection(mirror.mirror(dir)) : this;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    @Override
    public Component print(ChatFormatting defaultColor) {
        Component displayName = dir != null ? MoreCommonComponents.direction(dir) : VALUE_NONE;
        return displayName.copy().withStyle(defaultColor);
    }

    public static NullableDirection fromDirection(@Nullable Direction dir) {
        if (dir == null) {
            return NONE;
        }

        return switch (dir) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }
}
