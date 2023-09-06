package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

// TODO: implement
public final class TemplateElementBuilder extends ExtendedElementBuilder<TemplateElementBuilder>
{
    public TemplateElementBuilder()
    {
        super(ElementCategory.PARAGRAPH, "element");
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {

    }
}
