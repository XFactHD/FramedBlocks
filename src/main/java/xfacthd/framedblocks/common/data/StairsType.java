package xfacthd.framedblocks.common.data;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum StairsType implements IStringSerializable
{
    VERTICAL,
    TOP_CORNER,
    BOTTOM_CORNER;

    private final String name = toString().toLowerCase(Locale.ENGLISH);

    @Override
    public String getSerializedName() { return name; }

    public boolean isTop() { return this == TOP_CORNER; }

    public boolean isBottom() { return this == BOTTOM_CORNER; }
}