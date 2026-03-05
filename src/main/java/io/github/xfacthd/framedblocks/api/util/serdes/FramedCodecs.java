package io.github.xfacthd.framedblocks.api.util.serdes;

import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;

public final class FramedCodecs
{
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final Codec<Direction> DIRECTION_BY_INT = Codec.intRange(0, 5)
            .xmap(idx -> DIRECTIONS[idx], Direction::ordinal);

    private FramedCodecs() { }
}
