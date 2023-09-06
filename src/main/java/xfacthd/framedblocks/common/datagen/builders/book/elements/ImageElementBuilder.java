package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

public final class ImageElementBuilder extends ExtendedElementBuilder<ImageElementBuilder>
{
    private final String source;
    private Integer tx = null;
    private Integer ty = null;
    private Integer tw = null;
    private Integer th = null;

    public ImageElementBuilder(String source)
    {
        super(ElementCategory.PARAGRAPH, "image");
        this.source = source;
    }

    public ImageElementBuilder uv(int tx, int ty)
    {
        this.tx = tx;
        this.ty = ty;
        return this;
    }

    public ImageElementBuilder imgSize(int tw, int th)
    {
        this.tw = tw;
        this.th = th;
        return this;
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        elementElem.setAttribute("src", source);
        printNullableAttr(elementElem, "tx", tx);
        printNullableAttr(elementElem, "ty", ty);
        printNullableAttr(elementElem, "tw", tw);
        printNullableAttr(elementElem, "th", th);
    }
}
