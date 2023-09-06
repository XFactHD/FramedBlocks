package xfacthd.framedblocks.common.datagen.builders.book.chapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.includes.IncludeHolder;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.*;

import java.util.*;

public final class ChapterBuilder extends IncludeHolder<ChapterBuilder> implements Printable, Validatable, Identifiable
{
    private final String id;
    private final Map<String, SectionBuilder> sections = new LinkedHashMap<>();
    private String condition = null;

    public ChapterBuilder(String id)
    {
        this.id = id;
    }

    public ChapterBuilder section(SectionBuilder section)
    {
        SectionBuilder prev = sections.put(section.getName(), section);
        if (prev != null)
        {
            throw new IllegalStateException("Condition with name '" + prev.getName() + "' registered twice");
        }
        return this;
    }

    public ChapterBuilder condition(String condition)
    {
        this.condition = condition;
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
        sections.forEach((id, section) -> section.validate(book));
    }

    @Override
    public void print(Document doc, Element bookElem)
    {
        Element chapterElem = doc.createElement("chapter");
        chapterElem.setAttribute("id", id);
        if (condition != null)
        {
            chapterElem.setAttribute("condition", condition);
        }
        sections.forEach((id, section) -> section.print(doc, chapterElem));
        includes.forEach((name, inc) -> inc.print(doc, bookElem));
        bookElem.appendChild(chapterElem);
    }
}
