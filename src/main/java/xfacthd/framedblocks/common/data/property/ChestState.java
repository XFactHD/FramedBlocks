package xfacthd.framedblocks.common.data.property;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ChestState implements StringRepresentable
{
    CLOSED,
    OPENING,
    CLOSING;

    @Override
    public String getSerializedName() { return toString().toLowerCase(Locale.ROOT); }
}