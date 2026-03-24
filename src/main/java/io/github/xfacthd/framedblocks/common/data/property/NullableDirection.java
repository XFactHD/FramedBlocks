package io.github.xfacthd.framedblocks.common.data.property;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.function.IntFunction;

public enum NullableDirection implements StringRepresentable
{
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

    @Nullable
    private final Direction dir;

    NullableDirection(@Nullable Direction dir)
    {
        this.dir = dir;
    }

    public Direction toDirection()
    {
        return Objects.requireNonNull(dir);
    }

    @Nullable
    public Direction toNullableDirection()
    {
        return dir;
    }

    public NullableDirection rotate(Rotation rotation)
    {
        return dir != null ? fromDirection(rotation.rotate(dir)) : this;
    }

    public NullableDirection mirror(Mirror mirror)
    {
        return dir != null ? fromDirection(mirror.mirror(dir)) : this;
    }

    @Override
    public String getSerializedName()
    {
        return toString().toLowerCase(Locale.ROOT);
    }

    public static NullableDirection fromDirection(@Nullable Direction dir)
    {
        if (dir == null)
        {
            return NONE;
        }

        return switch (dir)
        {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }
}
