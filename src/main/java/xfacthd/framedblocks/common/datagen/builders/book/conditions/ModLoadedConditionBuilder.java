package xfacthd.framedblocks.common.datagen.builders.book.conditions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class ModLoadedConditionBuilder extends ConditionBuilder
{
    private final String modId;

    public ModLoadedConditionBuilder(String name, String modId)
    {
        super("mod-loaded", name);
        this.modId = modId;
    }

    @Override
    protected void printInternal(Document doc, Element condElem)
    {
        condElem.setAttribute("modid", modId);
    }
}
