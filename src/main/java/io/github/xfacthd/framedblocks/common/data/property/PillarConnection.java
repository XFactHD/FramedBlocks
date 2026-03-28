package io.github.xfacthd.framedblocks.common.data.property;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum PillarConnection implements StringRepresentable {
    NONE,
    POST,
    PILLAR;

    private final String name = toString().toLowerCase(Locale.ROOT);

    @Override
    public String getSerializedName() {
        return name;
    }
}
