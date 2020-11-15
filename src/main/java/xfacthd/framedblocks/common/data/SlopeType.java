package xfacthd.framedblocks.common.data;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum SlopeType implements IStringSerializable
{
    BOTTOM,
    HORIZONTAL,
    TOP;

    private final String name = toString().toLowerCase(Locale.ENGLISH);

    @Override
    public String getName() { return name; }
}