package xfacthd.framedblocks.common.datagen.builders.book;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.chapter.ChapterBuilder;
import xfacthd.framedblocks.common.datagen.builders.book.conditions.ConditionBuilder;
import xfacthd.framedblocks.common.datagen.builders.book.includes.IncludeBuilder;
import xfacthd.framedblocks.common.datagen.builders.book.includes.IncludeHolder;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.*;
import xfacthd.framedblocks.common.datagen.builders.book.stacklinks.StackLinkBuilder;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.util.*;

public final class GuidebookBuilder extends IncludeHolder<GuidebookBuilder> implements BookRoot, Identifiable
{
    private static final String STANDARD_INCLUDE = "gbook:xml/standard.xml";
    private static final ResourceLocation STANDARD_INCLUDE_LOC = ResourceLocation.parse(STANDARD_INCLUDE);

    private final String name;
    private final Map<String, ConditionBuilder> conditions = new LinkedHashMap<>();
    private final Map<String, ChapterBuilder> chapters = new LinkedHashMap<>();
    private final List<StackLinkBuilder> stackLinks = new ArrayList<>();
    private final Set<String> dependencies = new LinkedHashSet<>();
    private String title = null;
    private ResourceLocation cover = null;
    private ResourceLocation model = null;
    private ResourceLocation background = null;
    private Float fontSize = null;
    private String home = null;

    GuidebookBuilder(String name)
    {
        Preconditions.checkArgument(ResourceLocation.isValidPath(name), "Book name must be valid resource location path");
        this.name = name;
        this.includes.put(STANDARD_INCLUDE, new IncludeBuilder(STANDARD_INCLUDE_LOC));
    }

    public GuidebookBuilder title(String title)
    {
        this.title = title;
        return this;
    }

    public GuidebookBuilder cover(ResourceLocation cover)
    {
        this.cover = cover;
        return this;
    }

    public GuidebookBuilder model(ResourceLocation model)
    {
        this.model = model;
        return this;
    }

    public GuidebookBuilder background(ResourceLocation background)
    {
        this.background = background;
        return this;
    }

    public GuidebookBuilder fontSize(float fontSize)
    {
        this.fontSize = fontSize;
        return this;
    }

    public GuidebookBuilder home(String home)
    {
        this.home = home;
        return this;
    }

    public GuidebookBuilder dependency(String dependency)
    {
        dependencies.add(dependency);
        return this;
    }

    public GuidebookBuilder condition(ConditionBuilder condition)
    {
        ConditionBuilder prev = conditions.put(condition.getName(), condition);
        if (prev != null)
        {
            throw new IllegalStateException("Condition with name '" + prev.getName() + "' registered twice");
        }
        return this;
    }

    public GuidebookBuilder chapter(ChapterBuilder chapter)
    {
        ChapterBuilder prev = chapters.put(chapter.getName(), chapter);
        if (prev != null)
        {
            throw new IllegalStateException("Chapter with name '" + prev.getName() + "' registered twice");
        }
        return this;
    }

    public GuidebookBuilder stackLink(StackLinkBuilder stackLink)
    {
        stackLinks.add(stackLink);
        return this;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean hasCondition(String name)
    {
        return conditions.containsKey(name);
    }

    @Override
    public void validate()
    {
        chapters.forEach((id, chapter) -> chapter.validate(this));
    }

    void print(OutputStream output)
    {
        DocumentBuilder builder;
        Transformer transformer;
        try
        {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        }
        catch (ParserConfigurationException | TransformerConfigurationException e)
        {
            throw new RuntimeException(e);
        }

        Document doc = builder.newDocument();
        Element bookElem = doc.createElement("book");
        if (title != null)
        {
            bookElem.setAttribute("title", title);
        }
        if (cover != null)
        {
            bookElem.setAttribute("cover", cover.toString());
        }
        if (model != null)
        {
            bookElem.setAttribute("model", model.toString());
        }
        if (background != null)
        {
            bookElem.setAttribute("background", background.toString());
        }
        if (fontSize != null)
        {
            bookElem.setAttribute("fontSize", fontSize.toString());
        }
        if (home != null)
        {
            bookElem.setAttribute("home", home);
        }
        if (!dependencies.isEmpty())
        {
            bookElem.setAttribute("dependencies", String.join(",", dependencies));
        }
        includes.forEach((name, inc) -> inc.print(doc, bookElem));
        if (!conditions.isEmpty())
        {
            Element condElem = doc.createElement("conditions");
            conditions.forEach((name, cond) -> cond.print(doc, condElem));
            bookElem.appendChild(condElem);
        }
        chapters.forEach((id, chapter) -> chapter.print(doc, bookElem));
        if (!stackLinks.isEmpty())
        {
            Element stackLinksElem = doc.createElement("stack-links");
            stackLinks.forEach(link -> link.print(doc, stackLinksElem));
            bookElem.appendChild(stackLinksElem);
        }
        doc.appendChild(bookElem);

        StreamResult result = new StreamResult(output);
        try
        {
            transformer.transform(new DOMSource(doc), result);
        }
        catch (TransformerException e)
        {
            throw new RuntimeException(e);
        }
    }
}
