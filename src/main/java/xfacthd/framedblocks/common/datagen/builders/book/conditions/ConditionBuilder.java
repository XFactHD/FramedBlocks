package xfacthd.framedblocks.common.datagen.builders.book.conditions;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.Identifiable;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.Printable;

public abstract class ConditionBuilder implements Printable, Identifiable
{
    private final String type;
    private final String name;

    protected ConditionBuilder(String type, String name)
    {
        this.type = type;
        this.name = name;
    }

    @Override
    public final String getName()
    {
        return name;
    }

    @Override
    public final void print(Document doc, Element parentElem)
    {
        Element condElem = doc.createElement(type);
        condElem.setAttribute("name", name);
        printInternal(doc, condElem);
        parentElem.appendChild(condElem);
    }

    protected abstract void printInternal(Document doc, Element condElem);



    public static ConstantConditionBuilder constTrue(String name)
    {
        return new ConstantConditionBuilder(name, true);
    }

    public static ConstantConditionBuilder constFalse(String name)
    {
        return new ConstantConditionBuilder(name, false);
    }

    public static ModLoadedConditionBuilder modLoaded(String name, String modId)
    {
        return new ModLoadedConditionBuilder(name, modId);
    }

    public static ItemExistsConditionBuilder itemExists(String name, ItemLike item)
    {
        return itemExists(name, ForgeRegistries.ITEMS.getKey(item.asItem()));
    }

    public static ItemExistsConditionBuilder itemExists(String name, ResourceLocation itemId)
    {
        return new ItemExistsConditionBuilder(name, itemId);
    }

    public static ReferenceConditionBuilder reference(String name, String ref)
    {
        return new ReferenceConditionBuilder(name, ref);
    }

    public static AdvancementConditionBuilder advancementLocked(String name, ResourceLocation advancement)
    {
        return new AdvancementConditionBuilder(name, advancement, false);
    }

    public static AdvancementConditionBuilder advancementUnlocked(String name, ResourceLocation advancement)
    {
        return new AdvancementConditionBuilder(name, advancement, true);
    }

    public static CompoundConditionBuilder any(String name)
    {
        return new CompoundConditionBuilder.Any(name);
    }

    public static CompoundConditionBuilder all(String name)
    {
        return new CompoundConditionBuilder.All(name);
    }

    public static CompoundConditionBuilder not(String name)
    {
        return new CompoundConditionBuilder.Not(name);
    }
}
