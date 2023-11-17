package xfacthd.framedblocks.common.datagen.builders.book.elements;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.ForgeRegistries;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

public final class Elements
{
    public static SimpleContainerElementBuilder title()
    {
        return new SimpleContainerElementBuilder(ElementCategory.PAGE, "title", ElementCategory.PARAGRAPH);
    }

    public static SimpleContainerElementBuilder paragraph()
    {
        return new SimpleContainerElementBuilder(ElementCategory.PAGE, "p", ElementCategory.PARAGRAPH);
    }

    public static LinkElementBuilder link(String text)
    {
        return new LinkElementBuilder(text);
    }

    public static ImageElementBuilder image(String source)
    {
        return new ImageElementBuilder(source);
    }

    public static StackElementBuilder stack(ItemLike item)
    {
        return stack(ForgeRegistries.ITEMS.getKey(item.asItem()));
    }

    public static StackElementBuilder stack(ResourceLocation itemId)
    {
        return new StackElementBuilder(itemId);
    }

    public static SimpleElementBuilder sectionBreak()
    {
        return new SimpleElementBuilder(ElementCategory.PAGE, "section-break");
    }

    public static SimpleElementBuilder divider()
    {
        return new SimpleElementBuilder(ElementCategory.PAGE, "divider");
    }

    public static SimpleElementBuilder linebreak()
    {
        return new SimpleElementBuilder(ElementCategory.PARAGRAPH, "br");
    }

    public static SpaceElementBuilder spaceAbs(int height)
    {
        return new SpaceElementBuilder(Integer.toString(height));
    }

    public static SpaceElementBuilder spaceRel(int height)
    {
        return new SpaceElementBuilder(height + "%");
    }

    public static GridElementBuilder grid()
    {
        return new GridElementBuilder();
    }

    public static RowElementBuilder row()
    {
        return new RowElementBuilder();
    }

    public static ColumnElementBuilder column()
    {
        return new ColumnElementBuilder();
    }

    public static PanelElementBuilder panel()
    {
        return new PanelElementBuilder();
    }

    public static RecipeElementBuilder recipe()
    {
        return new RecipeElementBuilder();
    }

    public static SimpleContainerElementBuilder span()
    {
        return new SimpleContainerElementBuilder(ElementCategory.PARAGRAPH, "span", ElementCategory.PARAGRAPH);
    }

    public static TemplateElementBuilder template()
    {
        return new TemplateElementBuilder();
    }

    public static LiteralElementBuilder literal(String text)
    {
        return new LiteralElementBuilder(text);
    }

    public static TranslationElementBuilder translation(String langKey)
    {
        return new TranslationElementBuilder(langKey);
    }



    private Elements() { }
}
