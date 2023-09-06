package xfacthd.framedblocks.common.datagen.builders.book.elements;

import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

public final class RecipeElementBuilder extends ExtendedElementBuilder<RecipeElementBuilder>
{
    private ResourceLocation type = null;
    private ResourceLocation key = null;
    private Integer index = null;
    private StackElementBuilder result = null;

    public RecipeElementBuilder()
    {
        super(ElementCategory.PARAGRAPH, "recipe");
    }

    public RecipeElementBuilder type(ResourceLocation type)
    {
        this.type = type;
        return this;
    }

    public RecipeElementBuilder key(ResourceLocation key)
    {
        this.key = key;
        return this;
    }

    public RecipeElementBuilder index(int index)
    {
        this.index = index;
        return this;
    }

    public RecipeElementBuilder result(StackElementBuilder result)
    {
        this.result = result;
        return this;
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        printNullableAttr(elementElem, "type", type, ResourceLocation::toString);
        printNullableAttr(elementElem, "key", key, ResourceLocation::toString);
        printNullableAttr(elementElem, "index", index);
        if (result != null)
        {
            Element resultElem = doc.createElement("recipe.result");
            result.print(doc, resultElem);
            elementElem.appendChild(resultElem);
        }
    }
}
