package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

public final class SimpleElementBuilder extends ExtendedElementBuilder<SimpleElementBuilder>
{
    public SimpleElementBuilder(ElementCategory elemCat, String type)
    {
        super(elemCat, type);
    }

    @Override
    protected void printInternal(Document doc, Element elementElem) { }
}
