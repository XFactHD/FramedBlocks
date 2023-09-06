package xfacthd.framedblocks.common.datagen.builders.book.conditions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class ConstantConditionBuilder extends ConditionBuilder
{
    public ConstantConditionBuilder(String name, boolean value)
    {
        super(Boolean.toString(value), name);
    }

    @Override
    protected void printInternal(Document doc, Element condElem) { }
}
