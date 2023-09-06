package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

public final class ColumnElementBuilder extends ContainerElementBuilder<ColumnElementBuilder>
{
    private String width = null;
    private Integer colspan = null;

    public ColumnElementBuilder()
    {
        super(ElementCategory.SPECIAL, "col", ElementCategory.PAGE);
    }

    public ColumnElementBuilder widthAbs(int width)
    {
        this.width = Integer.toString(width);
        return this;
    }

    public ColumnElementBuilder widthRel(int width)
    {
        this.width = width + "%";
        return this;
    }

    public ColumnElementBuilder colspan(int colspan)
    {
        this.colspan = colspan;
        return this;
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        printNullableAttr(elementElem, "width", width);
        printNullableAttr(elementElem, "colspan", colspan);
        super.printInternal(doc, elementElem);
    }
}
