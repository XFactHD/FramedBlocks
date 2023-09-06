package xfacthd.framedblocks.common.datagen.builders.book.conditions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

public abstract class CompoundConditionBuilder extends ConditionBuilder
{
    private final Map<String, ConditionBuilder> nested = new LinkedHashMap<>();

    protected CompoundConditionBuilder(String type, String name)
    {
        super("any", name);
    }

    public CompoundConditionBuilder addCondition(ConditionBuilder condition)
    {
        ConditionBuilder prev = nested.put(condition.getName(), condition);
        if (prev != null)
        {
            throw new IllegalStateException("Condition with name '" + prev.getName() + "' registered twice");
        }
        return this;
    }

    @Override
    protected void printInternal(Document doc, Element condElem)
    {
        nested.forEach((name, cond) -> cond.print(doc, condElem));
    }



    public static final class Any extends CompoundConditionBuilder
    {
        public Any(String name)
        {
            super("any", name);
        }
    }

    public static final class All extends CompoundConditionBuilder
    {
        public All(String name)
        {
            super("all", name);
        }
    }

    public static final class Not extends CompoundConditionBuilder
    {
        public Not(String name)
        {
            super("not", name);
        }
    }
}
