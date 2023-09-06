package xfacthd.framedblocks.common.datagen.builders.book.includes;

import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.Identifiable;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.Printable;

public final class IncludeBuilder implements Printable, Identifiable
{
    private final ResourceLocation include;

    public IncludeBuilder(ResourceLocation include)
    {
        this.include = include;
    }

    @Override
    public String getName()
    {
        return include.toString();
    }

    @Override
    public void print(Document doc, Element parentElem)
    {
        Element incElem = doc.createElement("include");
        incElem.setAttribute("ref", include.toString());
        parentElem.appendChild(incElem);
    }
}
