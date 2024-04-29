package xfacthd.framedblocks.common.data.property;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
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

    private final Direction dir;

    NullableDirection(Direction dir)
    {
        this.dir = dir;
    }

    public Direction toDirection()
    {
        return dir;
    }

    @Override
    public String getSerializedName()
    {
        return toString().toLowerCase(Locale.ROOT);
    }



    public static NullableDirection fromDirection(Direction dir)
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