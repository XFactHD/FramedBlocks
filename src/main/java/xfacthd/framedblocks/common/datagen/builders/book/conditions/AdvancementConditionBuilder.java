package xfacthd.framedblocks.common.datagen.builders.book.conditions;

import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class AdvancementConditionBuilder extends ConditionBuilder
{
    private final ResourceLocation advancement;

    public AdvancementConditionBuilder(String name, ResourceLocation advancement, boolean unlocked)
    {
        super(unlocked ? "advancement-unlocked" : "advancement-locked", name);
        this.advancement = advancement;
    }

    @Override
    protected void printInternal(Document doc, Element condElem)
    {
        condElem.setAttribute("advancement", advancement.toString());
    }
}
