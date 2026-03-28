package io.github.xfacthd.framedblocks.common.data.property;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ChainType implements StringRepresentable {
    CAMO,
    METAL,
    NONE;

    private final String name = toString().toLowerCase(Locale.ROOT);

    @Override
    public String getSerializedName() {
        return name;
    }

    public ChainType next() {
        return switch (this) {
            case CAMO -> METAL;
            case METAL -> NONE;
            case NONE -> CAMO;
        };
    }
}
