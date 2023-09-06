package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

import java.util.Locale;

public final class PanelElementBuilder extends ContainerElementBuilder<PanelElementBuilder>
{
    private String height = null;
    private Mode mode = null;

    public PanelElementBuilder()
    {
        super(ElementCategory.PAGE, "panel", ElementCategory.PAGE);
    }

    public PanelElementBuilder heightAbs(int height)
    {
        this.height = Integer.toString(height);
        return this;
    }

    public PanelElementBuilder heightRel(int height)
    {
        this.height = height + "%";
        return this;
    }

    public PanelElementBuilder mode(Mode mode)
    {
        this.mode = mode;
        return this;
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        printNullableAttr(elementElem, "height", height);
        printNullableAttr(elementElem, "mode", mode);
        super.printInternal(doc, elementElem);
    }



    public enum Mode
    {
        DEFAULT,
        FLOW;

        private final String name = name().toLowerCase(Locale.ROOT);

        @Override
        public String toString()
        {
            return name;
        }
    }
}
