package xfacthd.framedblocks.common.datagen.builders.book.conditions;

import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class ItemExistsConditionBuilder extends ConditionBuilder
{
    private final ResourceLocation itemId;

    public ItemExistsConditionBuilder(String name, ResourceLocation itemId)
    {
        super("item-exists", name);
        this.itemId = itemId;
    }

    @Override
    protected void printInternal(Document doc, Element condElem)
    {
        condElem.setAttribute("registry-name", itemId.toString());
    }
}
