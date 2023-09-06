package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

import java.util.ArrayList;
import java.util.List;

public abstract class ContainerElementBuilder<T extends ContainerElementBuilder<T>> extends ExtendedElementBuilder<T>
{
    protected final List<ElementBuilder> childElements = new ArrayList<>();
    private final ElementCategory allowedCat;

    protected ContainerElementBuilder(ElementCategory elemCat, String type, ElementCategory allowedCat)
    {
        super(elemCat, type);
        this.allowedCat = allowedCat;
    }

    public T element(ElementBuilder element)
    {
        if (!allowedCat.allows(element.getCategory()))
        {
            throw new IllegalArgumentException(
                    "ContainerElement '%s' does not support element '%s' with category '%s'".formatted(
                            getClass().getSimpleName(),
                            element.getClass().getSimpleName(),
                            element.getCategory()
                    )
            );
        }

        childElements.add(element);
        //noinspection unchecked
        return (T) this;
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        childElements.forEach(child -> child.print(doc, elementElem));
    }
}
