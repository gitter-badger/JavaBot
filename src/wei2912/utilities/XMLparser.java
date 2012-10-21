package wei2912.utilities;

//~--- non-JDK imports --------------------------------------------------------

import org.w3c.dom.Document;

import org.xml.sax.InputSource;

//~--- JDK imports ------------------------------------------------------------

//~--- non-JDK imports --------------------------------------------------------
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLparser {
    Document document;

    public XMLparser(String name) {
        this.document = XMLparser.getDocument(name);
    }

    protected static Document getDocument(String name) {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setValidating(true);

            final DocumentBuilder builder = factory.newDocumentBuilder();

            return builder.parse(new InputSource(name));
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void saveFile(String name) {
        try {

            // write the content into xml file
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer        transformer        = transformerFactory.newTransformer();
            final DOMSource          source             = new DOMSource(this.document);
            final StreamResult       result             = new StreamResult(new File(name + ".xml"));

            transformer.transform(source, result);
            System.out.println("File saved!");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com


//~ Formatted by Jindent --- http://www.jindent.com
