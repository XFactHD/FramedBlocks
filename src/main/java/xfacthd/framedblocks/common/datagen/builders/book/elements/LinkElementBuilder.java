package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

public final class LinkElementBuilder extends ExtendedElementBuilder<LinkElementBuilder>
{
    private final String content;
    private String ref = null;
    private String href = null;
    private String text = null;
    private String action = null;

    public LinkElementBuilder(String content)
    {
        super(ElementCategory.PARAGRAPH, "link");
        this.content = content;
    }

    public LinkElementBuilder ref(String ref)
    {
        this.ref = ref;
        return this;
    }

    public LinkElementBuilder href(String href)
    {
        this.href = href;
        return this;
    }

    public LinkElementBuilder action(String action, String text)
    {
        this.action = action;
        this.text = text;
        return this;
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        if (ref != null)
        {
            elementElem.setAttribute("ref", ref);
        }
        if (href != null)
        {
            elementElem.setAttribute("href", href);
        }
        if (action != null && text != null)
        {
            elementElem.setAttribute("action", action);
            elementElem.setAttribute("text", text);
        }
        elementElem.appendChild(doc.createTextNode(content));
    }
}
