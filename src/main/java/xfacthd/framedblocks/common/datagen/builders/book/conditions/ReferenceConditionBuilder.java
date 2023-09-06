package xfacthd.framedblocks.common.datagen.builders.book.conditions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class ReferenceConditionBuilder extends ConditionBuilder
{
    private final String ref;

    public ReferenceConditionBuilder(String name, String ref)
    {
        super("condition", name);
        this.ref = ref;
    }

    @Override
    protected void printInternal(Document doc, Element condElem)
    {
        condElem.setAttribute("condition", ref);
    }
}
