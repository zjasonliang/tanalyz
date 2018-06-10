package temp.boilerpipe_demo;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

import java.io.PrintWriter;
import java.net.URL;


/**
 * Demonstrates how to use Boilerpipe to get the main content, highlighted as HTML.
 *
 */
public class HTMLHighlightDemo {
    public static void main(String[] args) throws Exception {
        URL url =
                new URL(
                        "https://en.wikipedia.org/wiki/Neuroscience");

        final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;

        // choose the operation mode (i.e., highlighting or extraction)
        // final HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance();
        final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();

        PrintWriter out = new PrintWriter("highlighted.html", "UTF-8");
        out.println("<base href=\"" + url + "\" >");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
        out.println(hh.process(url, extractor));
        out.close();
    }
}