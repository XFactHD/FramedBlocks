package xfacthd.framedblocks.common.data.property;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum StairsType implements StringRepresentable
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