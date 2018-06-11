package services.highlight;

import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import services.nlp.NER;
import services.nlp.POS;
import services.nlp.Text;

import javax.print.Doc;
import javax.xml.crypto.Data;

/**
 * This class highlight the HTML files
 */
public class HTMLHighlighter {

    private String RED_STYLE = "style=\"background-color:red \"";
    private String VIOLET_STYLE = "style=\"background-color:violet\"";
    private String GREEN_STYLE = "style=\"background-color:GREEN;COLOR:YELLOW \"";
    private String BLUE_STYLE = "style=\"background-color:BLUE;COLOR:VIOLET \"";
    private String DEFAULT_STYLE = "style=\"bacground-color:white;color:black \"";
    // String _STYLE = "style=\"bacground-color: \""
    // String _STYLE = "style=\"bacground-color: \""
    // String _STYLE = "style=\"bacground-color: \""

    private String styleString;

    /**
     * This method returns a html file that some words are highlighted
     *
     * @param word  the word need to be highlighted
     * @param color the color that the word  changed
     * @param text  a html file
     * @return a html file
     */
    public String highlightWord(String word, String text, Color color) {
        Document document = Jsoup.parse(text);
        Elements elements = document.body().getAllElements();
        getStyleString(color);
        for (Element ele : elements) {
            List <TextNode> tnList = ele.textNodes();
            for (TextNode tn : tnList) {
                String orig = tn.text();
                DataNode dataNode = new DataNode(orig.replace(word, "<mark " + styleString + ">" + word + "</mark>"));
                tn.replaceWith(dataNode);
            }
        }
        return document.toString();
    }


    public static String highlightWordsInExtractedHTML(Text text, List<POS> posList, List<NER> nerList) {
        Document document = Jsoup.parse(text.getWordSegmentedExtractedHTML());
        Elements elements = document.body().getAllElements();

        // System.out.println(text.getPosWordsMap().toString());
        // System.out.println(text.getNerWordsMap().toString());


        String separator = text.getWordSeparatorForExtractedHTML();



        for (POS pos : posList) {

            // System.out.println("===POS" + pos);
            // System.out.println(text.getPosWordsMap().get(pos));

            for (String word : text.getPosWordsMap().get(pos)) {

                String pattern = String.format("%s%s%s|%s%s|%s%s",
                        separator, word, separator,
                        separator, word,
                        word, separator);

                String replacement = separator + "<span " + pos.colorStyle + ">" + word + "</span>" + separator;


                // System.out.println("elements == " + elements.size());

                for (Element element : elements) {
                    List <TextNode> textNodeList = element.textNodes();

                    // System.out.println(textNodeList);

                    for (TextNode textNode : textNodeList) {
                        String original = textNode.text();
                        // System.out.println("==========");
                        // System.out.println(word);
                        // System.out.println(original);
                        // TextNode newTextNode = new TextNode(original.replaceAll(separator + word + separator, separator + "<span " + pos.colorStyle + ">" + word + "</span>" + separator));
                        TextNode newTextNode = new TextNode(original.replaceAll(pattern, replacement));
                        textNode.replaceWith(newTextNode);
                        // System.out.println(textNode.text());
                    }
                }
            }
        }

        for (NER ner : nerList) {
            for (String word : text.getNerWordsMap().get(ner)) {

                String pattern = String.format("%s%s%s|%s%s|%s%s",
                        separator, word, separator,
                        separator, word,
                        word, separator);

                String replacement = separator + "<span " + ner.colorStyle + ">" + word + "</span>" + separator;

                for (Element element : elements) {
                    List <TextNode> textNodeList = element.textNodes();
                    for (TextNode textNode : textNodeList) {
                        String original = textNode.text();
                        // TextNode newTextNode = new TextNode(original.replaceAll(separator + word + separator, separator + "<span " + ner.colorStyle + ">" + word + "</span>" + separator));
                        TextNode newTextNode = new TextNode(original.replaceAll(pattern, replacement));
                        textNode.replaceWith(newTextNode);
                    }
                }
            }
        }


        for (Element element : elements) {
            List <TextNode> textNodeList = element.textNodes();
            for (TextNode textNode : textNodeList) {
                String original = textNode.text();
                DataNode dataNode = new DataNode(original.replace("`", ""));
                textNode.replaceWith(dataNode);
            }
        }

        // System.out.println(document.toString());
        return document.toString();
    }

    /**
     * This method returns a html file that some patterns are highlighted
     *
     * @param pattern the pattern need to be highlighted
     * @param color   the color that the word  changed
     * @param text    a html file
     * @return a html file
     */
    public String highlightRegEx(String pattern, String text, Color color) {
        Document document = Jsoup.parse(text);
        Elements elements = document.body().getAllElements();
        getStyleString(color);
        for (Element ele : elements) {
            List <TextNode> tnList = ele.textNodes();
            for (TextNode tn : tnList) {
                String orig = tn.text();
                DataNode dataNode = new DataNode(orig.replaceAll(pattern, "<mark " + styleString + ">" + "$0" + "</mark>"));
                tn.replaceWith(dataNode);
            }

        }
        return document.toString();
    }

    /**
     * change one color into a style that can change background and frontground together
     *
     * @param color a color which is defined in enum color
     */
    private void getStyleString(Color color) {
        switch (color) {
            case RED:
                styleString = RED_STYLE;
                break;
            case YELLOW:
                styleString = "";
            case VIOLET:
                styleString = VIOLET_STYLE;
                break;
            case GREEN:
                styleString = GREEN_STYLE;
                break;
            case BLUE:
                styleString = BLUE_STYLE;
                break;
            default:
                break;
        }
    }
}