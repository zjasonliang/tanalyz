package services.util;

import de.l3s.boilerpipe.BoilerpipeDocumentSource;
import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;
import de.l3s.boilerpipe.sax.HTMLDocument;
import de.l3s.boilerpipe.sax.HTMLHighlighter;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;

public class HTMLHelper {

    public static void getDocumentString(Document document) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;

        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "html");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        StringWriter stringWriter = new StringWriter();

        try {
            transformer.transform(new DOMSource(document),
                    new StreamResult(stringWriter));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Print HTML Document
     *
     * @param document org.w3c.dom.Document object
     * @param out e.g. System.out
     */
    public static void printDocument(Document document, OutputStream out) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;

        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "html");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        try {
            transformer.transform(new DOMSource(document),
                    new StreamResult(new OutputStreamWriter(out, "UTF-8")));
        } catch (TransformerException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void saveDocument(Document document, File outFile) {
        try {
            printDocument(document, new FileOutputStream(outFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean isDocumentNull(Document document) {
        if (document == null) return true;
        if ("about:blank".equals(document.getDocumentURI())) return true;
        return false;
    }

    /**
     * Use boilerpipe to extract articles from an HTML document, and then return
     * only the extracted HTML text, including enclosed markup.
     *
     * @return an HTML that contains the extracted articles
     */
    public static String getExtractedHTMLString(URL url) throws Exception {
        final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;

        final HTMLHighlighter htmlHighlighter = HTMLHighlighter.newExtractingInstance();

        return "<base href=\"" + url + "\" >"
                + "<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />"
                + htmlHighlighter.process(url, extractor);
    }


    /**
     * return plain text
     *
     * @param originalHTML
     * @return
     * @throws Exception
     */
    public static String getExtractedText(String originalHTML) {
        String plainText = null;
        try {
            plainText = ArticleExtractor.getInstance().getText(originalHTML);
        } catch (BoilerpipeProcessingException e) {
            e.printStackTrace();
        }
        return plainText;
    }


    // TODO: HTMLDocument --> ???
    /**
     * Use boilerpipe to extract article from an HTML document and then print it.
     *
     * @param document
     */
    public static void printExtractedDocument(HTMLDocument document) throws Exception {
        final TextDocument textDocument =  new BoilerpipeSAXInput(document.toInputSource()).getTextDocument();
        String content = CommonExtractors.ARTICLE_EXTRACTOR.getText(textDocument);
        System.out.println(content);
    }
}
