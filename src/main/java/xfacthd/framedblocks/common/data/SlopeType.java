package xfacthd.framedblocks.common.data;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum SlopeType implements StringRepresentable
{
    BOTTOM,
    HORIZONTAL,
    TOP;

    private final String name = toString().toLowerCase(Locale.ENGLISH);

    @Override
    public String getSerializedName() { return name; }

    public SlopeType getOpposite()
    {
        return switch (this)
        {
            case TOP -> BOTTOM;
            case BOTTOM -> TOP;
            default -> throw new IllegalArgumentException("Can't get opposite of '" + getSerializedName() + "'!");
        };
    }
}