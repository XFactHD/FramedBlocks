package xfacthd.framedblocks.common.datagen.builders.book.elements.attributes;

public enum ElementCategory
{
    PAGE,
    PARAGRAPH,
    SPECIAL;

    public boolean allows(ElementCategory other)
    {
        if (this == SPECIAL || other == SPECIAL)
        {
            return this == other;
        }
        return other.ordinal() >= this.ordinal();
    }
}
