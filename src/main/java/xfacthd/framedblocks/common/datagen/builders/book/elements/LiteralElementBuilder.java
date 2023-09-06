package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.BookRoot;

public final class LiteralElementBuilder extends ElementBuilder
{
    private final String text;

    public LiteralElementBuilder(String text)
    {
        super(ElementCategory.PARAGRAPH);
        this.text = text;
    }

    @Override
    public void validate(BookRoot book) { }

    @Override
    public void print(Document doc, Element parentElem)
    {
        parentElem.appendChild(doc.createTextNode(text));
    }
}
