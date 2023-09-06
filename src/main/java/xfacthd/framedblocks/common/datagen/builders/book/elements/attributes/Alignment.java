package xfacthd.framedblocks.common.datagen.builders.book.elements.attributes;

import java.util.Locale;

public enum Alignment
{
    RELATIVE,
    ABSOLUTE,
    FIXED;

    private final String name = name().toLowerCase(Locale.ROOT);

    @Override
    public String toString()
    {
        return name;
    }
}
