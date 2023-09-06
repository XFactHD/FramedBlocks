package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

import java.util.ArrayList;
import java.util.List;

public final class RowElementBuilder extends ExtendedElementBuilder<RowElementBuilder>
{
    private final List<ColumnElementBuilder> columns = new ArrayList<>();
    private String height = null;

    public RowElementBuilder()
    {
        super(ElementCategory.SPECIAL, "row");
    }

    public RowElementBuilder heightAbs(int height)
    {
        this.height = Integer.toString(height);
        return this;
    }

    public RowElementBuilder heightRel(int height)
    {
        this.height = height + "%";
        return this;
    }

    public RowElementBuilder column(ColumnElementBuilder column)
    {
        columns.add(column);
        return this;
    }

    int getColumnCount()
    {
        return columns.size();
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        printNullableAttr(elementElem, "height", height);
        columns.forEach(col -> col.print(doc, elementElem));
    }
}
