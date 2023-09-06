package xfacthd.framedblocks.common.datagen.builders.book.elements;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

public final class StackElementBuilder extends ExtendedElementBuilder<StackElementBuilder>
{
    private final ResourceLocation itemId;
    private Integer count = null;
    private String name = null;
    private CompoundTag tag = null;

    public StackElementBuilder(ResourceLocation itemId)
    {
        super(ElementCategory.PARAGRAPH, "stack");
        this.itemId = itemId;
    }

    public StackElementBuilder count(int count)
    {
        this.count = count;
        return this;
    }

    public StackElementBuilder name(String name)
    {
        this.name = name;
        return this;
    }

    public StackElementBuilder tag(CompoundTag tag)
    {
        this.tag = tag;
        return this;
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        elementElem.setAttribute("item", itemId.toString());
        printNullableAttr(elementElem, "count", count);
        printNullableAttr(elementElem, "name", name);
        printNullableAttr(elementElem, "tag", tag, CompoundTag::getAsString);
    }
}
