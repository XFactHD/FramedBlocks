package xfacthd.framedblocks.common.datagen.builders.book.primitives;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Printable
{
    void print(Document doc, Element parentElem);
}
