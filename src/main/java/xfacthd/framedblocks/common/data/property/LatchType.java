package xfacthd.framedblocks.common.data.property;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum LatchType implements StringRepresentable
{
    DEFAULT,
    CAMO,
    NONE;

    @Override
    public String getSerializedName()
    {
        return toString().toLowerCase(Locale.ROOT);
    }

    public LatchType next()
    {
        return switch (this)
        {
            case DEFAULT -> CAMO;
            case CAMO -> NONE;
            case NONE -> DEFAULT;
        };
    }
}
