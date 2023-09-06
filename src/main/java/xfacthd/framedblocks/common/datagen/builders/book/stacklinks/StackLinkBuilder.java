package xfacthd.framedblocks.common.datagen.builders.book.stacklinks;

import net.minecraft.resources.ResourceLocation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.primitives.Printable;

public final class StackLinkBuilder implements Printable
{
    private final ResourceLocation itemId;
    private final String ref;

    public StackLinkBuilder(ResourceLocation itemId, String ref)
    {
        this.itemId = itemId;
        this.ref = ref;
    }

    @Override
    public void print(Document doc, Element stackLinksElem)
    {
        Element linkElem = doc.createElement("stack");
        linkElem.setAttribute("item", itemId.toString());
        linkElem.appendChild(doc.createTextNode(ref));
        stackLinksElem.appendChild(linkElem);
    }
}
