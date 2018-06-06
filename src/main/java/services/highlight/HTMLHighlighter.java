package services.highlight;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/**
 * This class highlight the HTML files
 */
public class HTMLHighlighter {

    String RED_STYLE = "style=\"background-color:red \"";
    String VIOLET_STYLE = "style=\"background-color:violet\"";
    String GREEN_STYLE = "style=\"background-color:GREEN;COLOR:YELLOW \"";
    String BLUE_STYLE = "style=\"background-color:BLUE;COLOR:VIOLET \"";
    String DEFAULT_STYLE = "style=\"bacground-color:white;color:black \"";
    // String _STYLE = "style=\"bacground-color: \""
    // String _STYLE = "style=\"bacground-color: \""
    // String _STYLE = "style=\"bacground-color: \""

    String styleString;

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