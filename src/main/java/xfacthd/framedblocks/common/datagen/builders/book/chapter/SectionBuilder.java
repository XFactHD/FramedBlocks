package xfacthd.framedblocks.common.datagen.builders.book.chapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.*;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;
import xfacthd.framedblocks.common.datagen.builders.book.includes.IncludeHolder;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.*;

import java.util.*;

public final class SectionBuilder extends IncludeHolder<SectionBuilder> implements Printable, Validatable, Identifiable
{
    private final String type;
    private final String id;
    private final List<ElementBuilder> elements = new ArrayList<>();
    private SimpleContainerElementBuilder title = null;
    private String condition = null;

    public SectionBuilder(String type, String id)
    {
        this.type = type;
        this.id = id;
    }

    public SectionBuilder condition(String condition)
    {
        this.condition = condition;
        return this;
    }

    public SectionBuilder pageTitle(SimpleContainerElementBuilder title)
    {
        this.title = title;
        return this;
    }

    public SectionBuilder element(ElementBuilder element)
    {
        if (!ElementCategory.PAGE.allows(element.getCategory()))
        {
            throw new IllegalArgumentException("Element '%s' is not a page-level element".formatted(
                            getClass().getSimpleName()
            ));
        }

        elements.add(element);
        return this;
    }

    @Override
    public String getName()
    {
        return id;
    }

    @Override
    public void validate(BookRoot book)
    {
        if (condition != null && !book.hasCondition(condition))
        {
            throw new IllegalArgumentException("Unknown condition: " + condition);
        }
    }

    @Override
    public void print(Document doc, Element parentElem)
    {
        Element sectionElem = doc.createElement(type);
        sectionElem.setAttribute("id", id);
        if (condition != null)
        {
            sectionElem.setAttribute("condition", condition);
        }
        if (title != null)
        {
            Element pageTitleElem = doc.createElement("page_title");
            title.print(doc, pageTitleElem);
            sectionElem.appendChild(pageTitleElem);
        }
        elements.forEach(elem -> elem.print(doc, sectionElem));
        includes.forEach((name, inc) -> inc.print(doc, sectionElem));
        parentElem.appendChild(sectionElem);
    }
}
