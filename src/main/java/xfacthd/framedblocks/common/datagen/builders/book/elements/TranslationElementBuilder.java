package xfacthd.framedblocks.common.datagen.builders.book.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xfacthd.framedblocks.common.datagen.builders.book.elements.attributes.ElementCategory;

// TODO: check whether this is correct
public final class TranslationElementBuilder extends ExtendedElementBuilder<TranslationElementBuilder>
{
    private final String langKey;

    public TranslationElementBuilder(String langKey)
    {
        super(ElementCategory.PARAGRAPH, "translation");
        this.langKey = langKey;
    }

    @Override
    protected void printInternal(Document doc, Element elementElem)
    {
        elementElem.appendChild(doc.createTextNode(langKey));
    }
}
