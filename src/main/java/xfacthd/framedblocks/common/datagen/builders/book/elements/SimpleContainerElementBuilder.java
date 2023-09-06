package xfacthd.framedblocks.common.datagen.builders.book.elements;

import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

public final class SimpleContainerElementBuilder extends ContainerElementBuilder<SimpleContainerElementBuilder>
{
    public SimpleContainerElementBuilder(ElementCategory elemCat, String type, ElementCategory allowedCat)
    {
        super(elemCat, type, allowedCat);
    }
}
