package xfacthd.framedblocks.common.datagen.builders.book.elements;

import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.Printable;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.Validatable;

public abstract class ElementBuilder implements Printable, Validatable
{
    private final ElementCategory elemCat;

    protected ElementBuilder(ElementCategory elemCat)
    {
        this.elemCat = elemCat;
    }

    public ElementCategory getCategory()
    {
        return elemCat;
    }
}
