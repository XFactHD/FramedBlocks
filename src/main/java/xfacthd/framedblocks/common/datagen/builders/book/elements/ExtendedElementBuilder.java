package xfacthd.framedblocks.common.datagen.builders.book.elements;

import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.*;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.*;

import java.util.function.Function;

public abstract class ExtendedElementBuilder<T extends ExtendedElementBuilder<T>> extends ElementBuilder
{
    private final String type;
    private Integer x = null;
    private Integer y = null;
    private Integer z = null;
    private Integer w = null;
    private Integer h = null;
    private Float baseline = null;
    private Alignment align = null;
    private HorizontalAlignment horAlign = null;
    private VerticalAlignment vertAlign = null;
    private Integer indent = null;
    private Integer space = null;
    private String color = null;
    private Boolean bold = null;
    private Boolean italics = null;
    private Boolean underline = null;
    private Boolean strikethrough = null;
    private Boolean obfuscated = null;
    private ResourceLocation font = null;
    private Float scale = null;
    private String condition = null;

    protected ExtendedElementBuilder(ElementCategory elemCat, String type)
    {
        super(elemCat);
        this.type = type;
    }

    public final T posX(int x)
    {
        this.x = x;
        //noinspection unchecked
        return (T) this;
    }

    public final T posY(int y)
    {
        this.y = y;
        //noinspection unchecked
        return (T) this;
    }

    public final T posZ(int z)
    {
        this.z = z;
        //noinspection unchecked
        return (T) this;
    }

    public final T width(int w)
    {
        this.w = w;
        //noinspection unchecked
        return (T) this;
    }

    public final T height(int h)
    {
        this.h = h;
        //noinspection unchecked
        return (T) this;
    }

    public final T baseline(float baseline)
    {
        this.baseline = baseline;
        //noinspection unchecked
        return (T) this;
    }

    public final T align(Alignment align)
    {
        this.align = align;
        //noinspection unchecked
        return (T) this;
    }

    public final T horAlign(HorizontalAlignment align)
    {
        this.horAlign = align;
        //noinspection unchecked
        return (T) this;
    }

    public final T vertAlign(VerticalAlignment align)
    {
        this.vertAlign = align;
        //noinspection unchecked
        return (T) this;
    }

    public final T indent(int indent)
    {
        this.indent = indent;
        //noinspection unchecked
        return (T) this;
    }

    public final T space(int space)
    {
        this.space = space;
        //noinspection unchecked
        return (T) this;
    }

    public final T color(String hexColor)
    {
        this.color = hexColor;
        //noinspection unchecked
        return (T) this;
    }

    public final T bold(boolean bold)
    {
        this.bold = bold;
        //noinspection unchecked
        return (T) this;
    }

    public final T italics(boolean italics)
    {
        this.italics = italics;
        //noinspection unchecked
        return (T) this;
    }

    public final T underline(boolean underline)
    {
        this.underline = underline;
        //noinspection unchecked
        return (T) this;
    }

    public final T strikethrough(boolean strikethrough)
    {
        this.strikethrough = strikethrough;
        //noinspection unchecked
        return (T) this;
    }

    public final T obfuscated(boolean obfuscated)
    {
        this.obfuscated = obfuscated;
        //noinspection unchecked
        return (T) this;
    }

    public final T font(ResourceLocation font)
    {
        this.font = font;
        //noinspection unchecked
        return (T) this;
    }

    public final T scale(float scale)
    {
        this.scale = scale;
        //noinspection unchecked
        return (T) this;
    }

    public final T condition(String condition)
    {
        this.condition = condition;
        //noinspection unchecked
        return (T) this;
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
    public final void print(Document doc, Element parentElem)
    {
        Element elementElem = doc.createElement(type);

        printNullableAttr(elementElem, "x", x);
        printNullableAttr(elementElem, "y", y);
        printNullableAttr(elementElem, "z", z);
        printNullableAttr(elementElem, "w", w);
        printNullableAttr(elementElem, "h", h);
        printNullableAttr(elementElem, "baseline", baseline);
        printNullableAttr(elementElem, "align", horAlign != null ? horAlign : align);
        printNullableAttr(elementElem, "vertical-align", vertAlign);
        printNullableAttr(elementElem, "indent", indent);
        printNullableAttr(elementElem, "space", space);
        printNullableAttr(elementElem, "color", color);
        printNullableAttr(elementElem, "bold", bold);
        printNullableAttr(elementElem, "italics", italics);
        printNullableAttr(elementElem, "underline", underline);
        printNullableAttr(elementElem, "strikethrough", strikethrough);
        printNullableAttr(elementElem, "obfuscated", obfuscated);
        printNullableAttr(elementElem, "font", font);
        printNullableAttr(elementElem, "scale", scale);
        printNullableAttr(elementElem, "condition", condition);

        printInternal(doc, elementElem);
        parentElem.appendChild(elementElem);
    }

    protected static <T> void printNullableAttr(Element elem, String attr, T value)
    {
        printNullableAttr(elem, attr, value, Object::toString);
    }

    protected static <T> void printNullableAttr(Element elem, String attr, T value, Function<T, String> stringifier)
    {
        if (value != null)
        {
            elem.setAttribute(attr, stringifier.apply(value));
        }
    }

    protected abstract void printInternal(Document doc, Element elementElem);
}
