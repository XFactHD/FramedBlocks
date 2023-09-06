package xfacthd.framedblocks.common.datagen.builders.book.elements.attributes;

import java.util.Locale;

public enum HorizontalAlignment
{
    LEFT,
    CENTER,
    RIGHT;

    private final String name = name().toLowerCase(Locale.ROOT);

    @Override
    public String toString()
    {
        return name;
    }
}
