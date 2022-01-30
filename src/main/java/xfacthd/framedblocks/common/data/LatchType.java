package xfacthd.framedblocks.common.data;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum LatchType implements IStringSerializable
{
    DEFAULT,
    CAMO,
    NONE;

    @Override
    public String getString() { return toString().toLowerCase(Locale.ROOT); }

    public LatchType next()
    {
        switch (this)
        {
            case DEFAULT: return CAMO;
            case CAMO: return NONE;
            case NONE: return DEFAULT;
            default: throw new IllegalArgumentException("Invalid LatchType: " + this);
        }
    }
}
