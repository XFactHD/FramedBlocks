package xfacthd.framedblocks.common.data;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum ChestState implements IStringSerializable
{
    CLOSED,
    OPENING,
    CLOSING;

    @Override
    public String getString() { return toString().toLowerCase(Locale.ROOT); }
}