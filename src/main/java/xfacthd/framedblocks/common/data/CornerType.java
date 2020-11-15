package xfacthd.framedblocks.common.data;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum CornerType implements IStringSerializable
{
    BOTTOM,
    TOP,
    HORIZONTAL_BOTTOM_LEFT,
    HORIZONTAL_BOTTOM_RIGHT,
    HORIZONTAL_TOP_LEFT,
    HORIZONTAL_TOP_RIGHT;

    private final String name = toString().toLowerCase(Locale.ENGLISH);

    @Override
    public String getName() { return name; }

    public boolean isHorizontal() { return this != BOTTOM && this != TOP; }

    public boolean isTop() { return this == TOP || this == HORIZONTAL_TOP_LEFT || this == HORIZONTAL_TOP_RIGHT; }

    public boolean isRight() { return this == HORIZONTAL_BOTTOM_RIGHT || this == HORIZONTAL_TOP_RIGHT; }
}