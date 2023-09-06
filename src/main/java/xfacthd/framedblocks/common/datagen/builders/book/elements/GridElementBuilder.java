package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.BookRoot;

import java.util.ArrayList;
import java.util.List;

public final class GridElementBuilder extends ExtendedElementBuilder<GridElementBuilder>
{
    private final List<RowElementBuilder> rows = new ArrayList<>();
    private String width = null;
    private String height = null;

    public GridElementBuilder()
    {
        super(ElementCategory.PAGE, "grid");
    }

    public GridElementBuilder widthAbs(int width)
    {
        this.width = Integer.toString(width);
        return this;
    }

    public GridElementBuilder widthRel(int width)
    {
        this.width = width + "%";
        return this;
    }

    public GridElementBuilder heightAbs(int height)
    {
        this.height = Integer.toString(height);
        return this;
    }

    public GridElementBuilder heightRel(int height)
    {
        this.height = height + "%";
        return this;
    }

    public GridElementBuilder row(RowElementBuilder row)
    {
        rows.add(row);
        return this;
    }

    @Override
    public void validate(BookRoot book)
    {
        super.validate(book);
        if (!rows.isEmpty())
        {
            int colCount = rows.get(0).getColumnCount();
            rows.forEach(row ->
            {
                if (row.getColumnCount() != colCount)
                {
                    throw new IllegalStateException("All rows in a grid must have the same number of columns!");
                }
            });
        }
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        printNullableAttr(elementElem, "width", width);
        printNullableAttr(elementElem, "height", height);
        rows.forEach(row -> row.print(doc, elementElem));
    }
}
