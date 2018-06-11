package services.nlp;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import services.util.HTMLHelper;

import java.util.*;

public class Text {

    class IndexTextPiecePair {
        Integer index;
        String textPiece;
    }

    private Map<String, WordFrequencyPair> wordFrequencyMap;
    private boolean isWordFrequencyMapInitialized;
    private List<WordFrequencyPair> wordFrequencySortedList;
    private boolean isWordFrequencySortedListInitialized;
    private boolean isExtracted;

    private String originalHTML;
    private String extractedHTML;
    private String extractedPlainText;

    private String wordSegmentedExtractedPlainText;
    private boolean isExtractedPlainTextWordSegmented;
    private String wordSeparatorForPlainText;

    private String wordSegmentedExtractedHTML;
    private boolean isExtractedHTMLWordSegmented;
    private String wordSeparatorForExtractedHTML;

    private Map<POS, Set<String>> posWordsMap;
    private Map<String, POS> wordPosMap;
    private boolean isPOSWordsMapGenerated;

    private Map<NER, Set<String>> nerWordsMap;
    private Map<String, NER> wordNerMap;
    private boolean isNERWordsMapGenerated;



    public Text(String originalHTML) {
        this.originalHTML = originalHTML;
        this.extractedPlainText = HTMLHelper.getExtractedText(originalHTML);
        this.isWordFrequencyMapInitialized = false;
        this.isWordFrequencySortedListInitialized = false;
    }

    private void wordSegmentExtractedPlainText(String separator) {
        wordSegmentedExtractedPlainText = NLPServices.wordSegmentAsString(extractedPlainText, separator);
        wordSeparatorForPlainText = separator;
    }

    private void ensureExtractedPainTextWordSegmented() {
        if (!isExtractedPlainTextWordSegmented) {
            wordSegmentExtractedPlainText("`");
            isExtractedPlainTextWordSegmented = true;
        }
    }


    private void wordSegmentExtractedHTML(String separator) {
        ensureExtractedHTML();

        Document document = Jsoup.parse(extractedHTML);
        Elements elements = document.body().getAllElements();

        wordSeparatorForExtractedHTML = separator;

        for (Element element : elements) {
            List <TextNode> textNodeList = element.textNodes();

            for (TextNode textNode : textNodeList) {
                String original = textNode.text();
                // System.out.println("====> " + original.trim() + "<=====");
                if (original.trim().length() > 10 ) {
                    DataNode dataNode = new DataNode(NLPServices.wordSegmentAsString(original, separator));
                    textNode.replaceWith(dataNode);
                }
            }
        }
        wordSegmentedExtractedHTML = document.toString();
    }

    private void ensureExtractedHTMLWordSegmented() {
        if (!isExtractedHTMLWordSegmented) {
            wordSegmentExtractedHTML("`");
            isExtractedHTMLWordSegmented = true;
        }
    }

    private void ensureExtractedHTML() {
        if (!isExtracted) {
            extractedHTML = HTMLHelper.getExtractedHTMLString(originalHTML);
            isExtracted = true;
        }
    }

    private void ensureWordSegmentedinExtractedHTML() {
        ensureExtractedHTML();
    }


    private void ensureWordFrequencySortedListInitialized() {
        ensureWordFrequencyMapInitialized();

        if (!isWordFrequencySortedListInitialized) {
            wordFrequencySortedList = new ArrayList <>(wordFrequencyMap.values());
            Collections.sort(wordFrequencySortedList, (o1, o2) -> o2.frequency - o1.frequency);
            isWordFrequencySortedListInitialized = true;
        }
    }

    private void ensureWordFrequencyMapInitialized() {
        if (!isWordFrequencyMapInitialized) {
            wordFrequencyMap = new HashMap <>();

            List<String> segmentedTextList = NLPServices.wordSegment(extractedPlainText, "`");

            for (String sentence : segmentedTextList) {
                String [] wordList = sentence.split("`");

                for (String word : wordList) {
                    if ("".equals(word)) continue;

                    if (wordFrequencyMap.containsKey(word))
                        wordFrequencyMap.put(word, new WordFrequencyPair(word, wordFrequencyMap.get(word).frequency + 1));
                    else
                        wordFrequencyMap.put(word, new WordFrequencyPair(word, 1));
                }
            }

            isWordFrequencyMapInitialized = true;
        }
    }

    public void ensureGeneratePOSWordsMap() {
        if (!isPOSWordsMapGenerated) {
            posWordsMap = new HashMap <>();
            wordPosMap = new HashMap <>();

            WordPOSPair [] wordPOSPairs = NLPServices.getPosTags(extractedPlainText);

            for (WordPOSPair pair : wordPOSPairs) {
                if (!posWordsMap.containsKey(pair.pos)) {
                    posWordsMap.put(pair.pos, new HashSet <>());
                }
                posWordsMap.get(pair.pos).add(pair.word);

                wordPosMap.put(pair.word, pair.pos);
            }

            isPOSWordsMapGenerated = true;
        }
    }

    public void ensureGenerateNERWordsMap() {
        if (!isNERWordsMapGenerated) {
            nerWordsMap = new HashMap <>();
            wordNerMap = new HashMap <>();

            WordNamedEntityPair [] wordNamedEntityPairs = NLPServices.getNamedEntities(extractedPlainText);
            for (WordNamedEntityPair pair : wordNamedEntityPairs) {
                if (!nerWordsMap.containsKey(pair.namedEntity)) {
                    nerWordsMap.put(pair.namedEntity, new HashSet <>());
                }
                nerWordsMap.get(pair.namedEntity).add(pair.word);

                wordNerMap.put(pair.word, pair.namedEntity);
            }

            isNERWordsMapGenerated = true;
        }
    }

    public Integer getWordFrequency(String word) {
        ensureWordFrequencyMapInitialized();
        return wordFrequencyMap.get(word).frequency;
    }


    public List<WordFrequencyPair> getWordFrequencySortedList() {
        ensureWordFrequencySortedListInitialized();
        return wordFrequencySortedList;
    }



    public String getExtractedPlainText() {
        return extractedPlainText;
    }

    public String getWordSegmentedExtractedPlainText() {
        return wordSegmentedExtractedPlainText;
    }

    public String getOriginalHTML() {
        return originalHTML;
    }

    public String getWordSegmentedExtractedHTML() {
        ensureExtractedHTMLWordSegmented();
        return wordSegmentedExtractedHTML;
    }

    public String getWordSeparatorForExtractedHTML() {
        return wordSeparatorForExtractedHTML;
    }

    public String getExtractedHTML() {
        ensureExtractedHTML();
        return extractedHTML;
    }

    public Map <POS, Set <String>> getPosWordsMap() {
        ensureGeneratePOSWordsMap();
        return posWordsMap;
    }

    public Map <String, POS> getWordPosMap() {
        ensureGeneratePOSWordsMap();
        return wordPosMap;
    }

    public Map <NER, Set <String>> getNerWordsMap() {
        ensureGenerateNERWordsMap();
        return nerWordsMap;
    }

    public Map <String, NER> getWordNerMap() {
        ensureGenerateNERWordsMap();
        return wordNerMap;
    }

    public static void main(String[] args) {
        String htmlString = "<!doctype html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta charset=\"utf-8\">\n" +
                "\n" +
                "\n" +
                "<head>\n" +
                "\n" +
                "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "\n" +
                "\t<title>北京大学</title>\n" +
                "\n" +
                "\t<meta content=\"北京大学作为国内前茅的文理医工综合性大学，在培养高素质创新型人才、取得突破性科研进展，以及为国民经济发展和社会进步提供智力支持等方面都发挥着极其重要的作用。\" name=\"description\">\n" +
                "\t<meta content=\"北京大学、北大、PKU、Beida、Peking University、北京大学新闻网、北大招生网、奥运平台、北大电视台、未名BBS、校友网、图书馆、元培计划、北大医学部、深圳研究生院、数学科学学院、物理学院、化学与分子工程学院、生命科学学院、信息科学技术学院、环境学院、工学院、中国语言文学系、历史学系、哲学系（宗教学系）、考古文博学院、外国语学院、国际关系学院、经济学院、中国经济研究中心、光华管理学院、新闻与传播学院、国际合作部、教务部、研究生院、科学研究部、社会科学部、人事部、团委、学生会、出版社、方正集团、青鸟集团、北大科技园\" name=\"keywords\">\n" +
                "\t<meta name=\"author\" content=\"北京大学计算中心\">\n" +
                "\t<meta name=\"copyright\" content=\"2014 Peking University. 北京大学 版权所有\">\n" +
                "\t<meta name=\"format-detection\" content=\"telephone=no\">\n" +
                "        <link href=\"../css/index.css\" type=\"text/css\" rel=\"stylesheet\" />\n" +
                "\t<link rel=\"shortcut icon\" href=\"/pku_favicon.ico\">\n" +
                "        <link rel=\"apple-touch-icon\" href=\"/pku_logo_red.png\" />\n" +
                "\t<link href=\"../files/base.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "\t<link href=\"../files/inner.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "\t<link href=\"../files/mmenu.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "\t<link href=\"../files/thuicon.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "        <link href=\"../css/leadertable.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "        <link href=\"../css/font-awesome.min.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "\t<script src=\"../files/jq.js\" type=\"text/javascript\"></script>\n" +
                "\t<script src=\"../files/comm.js\" type=\"text/javascript\"></script>\n" +
                "\t<script src=\"../files/inner.js\" type=\"text/javascript\"></script>\n" +
                "\t<!--[if lt IE 9]>\n" +
                "\t<script src=\"../files/html5.js\" type=\"text/javascript\"></script>\n" +
                "\t<script src=\"../files/response.min.js\"></script>\n" +
                "\t<link href=\"../files/mmenu2.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "\t<![endif]-->\n" +
                "\n" +
                "\t<link href=\"../css/pku.css\" rel=\"stylesheet\" type=\"text/css\">\n" +
                "\t<script src=\"../js/pku.js\" type=\"text/javascript\"></script>\n" +
                "\n" +
                "</head>\n" +
                "</body></html>\n" +
                "<body>\n" +
                "\n" +
                "<!--  pku_header_section_start   -->\n" +
                "<header class=\"header\">\n" +
                "\t<section>\n" +
                "\t\t<div class=\"topLine\"></div>\n" +
                "\t\t<section class=\"subNav yahei clearfix\">\n" +
                "               <!-- section class=\"ssubNav\">子导航</section -->\t\t\t\n" +
                "            <section class=\"ssubNavPKU show-md subNavlogo\">\n" +
                "                <span id=\"navline_1\" class=\"navline\"></span>\n" +
                "        \t\t<span id=\"navline_2\" class=\"navline\"></span>\n" +
                "        \t\t<span id=\"navline_3\" class=\"navline\" style=\"margin-bottom:3px;\"></span>\n" +
                "            </section>\t\t\n" +
                "            <section class=\"ssubNavPKU show-lg\">\n" +
                "                <ul class=\"subnavLeft\">\n" +
                "\t\t\t\t\t<li id=\"students\"><a href=\"../students/index.htm\">学生</a></li>\n" +
                "\t\t\t\t\t<li id=\"facultystaff\"><a href=\"../facultystaff/index.htm\">教职工</a></li>\n" +
                "\t\t\t\t\t<li id=\"alumni\"><a href=\"http://www.pku.org.cn/\">校友</a></li>\n" +
                "\t\t\t\t\t<li id=\"parents\"><a href=\"../parents/index.htm\">家长</a></li>\n" +
                "\t\t\t\t\t<li id=\"visitors\"><a href=\"../visitors/index.htm\">访客</a></li>\n" +
                "\t\t\t\t\t<li id=\"jobs\"><a href=\"http://hr.pku.edu.cn/rczp/js/\">招聘</a></li>\n" +
                "\t\t\t\t\t<li id=\"giving\"><a href=\"http://www.pkuef.org/pkuef/\">捐赠</a></li>\n" +
                "\t\t\t\t</ul>\n" +
                "                <div class=\"subNavRight\">\n" +
                "                    <li id=\"portal\"><a href=\"https://portal.pku.edu.cn/\">门户</a></li>\n" +
                "\t\t\t\t\t<li id=\"its\"><a href=\"https://its.pku.edu.cn/\">网络</a></li>\n" +
                "\t\t\t\t\t<li id=\"bbs\"><a href=\"http://bbs.pku.edu.cn/\">BBS</a></li>\n" +
                "                    <li class=\"subNavlogo\"  style=\"padding-left:17px;padding-right:12px;padding-top:9px;\">\n" +
                "                       <span id=\"navline_1_1\" class=\"navline\"></span>\n" +
                "            \t\t   <span id=\"navline_1_2\" class=\"navline\"></span>\n" +
                "            \t\t   <span id=\"navline_1_3\" class=\"navline\" style=\"margin-bottom:3px;\"></span>\n" +
                "                     </li>\n" +
                "                </div>\n" +
                "            </section>\n" +
                "        \n" +
                "        \n" +
                "\t\t\t<section class=\"mainWrap mainWrap02 noline\">\n" +
                "\t\t\t\t<ul id=\"subNav\" class=\"subnavLeft\">\n" +
                "\t\t\t\t\t<li id=\"students\" ><a href=\"../students/index.htm\">学生</a></li>\n" +
                "\t\t\t\t\t<li id=\"facultystaff\" ><a href=\"../facultystaff/index.htm\">教职工</a></li>\n" +
                "\t\t\t\t\t<li id=\"alumni\"><a href=\"http://www.pku.org.cn/\">校友</a></li>\n" +
                "\t\t\t\t\t<li id=\"parents\" ><a href=\"../parents/index.htm\">家长</a></li>\n" +
                "\t\t\t\t\t<li id=\"visitors\" ><a href=\"../visitors/index.htm\">访客</a></li>\n" +
                "\t\t\t\t\t<li id=\"jobs\"><a href=\"http://hr.pku.edu.cn/rczp/js/\">招聘</a></li>\n" +
                "\t\t\t\t\t<li id=\"giving\"><a href=\"http://www.pkuef.org/pkuef/\">捐赠</a></li>\n" +
                "\t\t\t\t</ul>\n" +
                "\t\t\t\n" +
                "\t\t\t\t<ul class=\"subNavRight\" style=\"padding-right:12px;\">\n" +
                "\t\t\t\t\t<li id=\"portal\"><a href=\"https://portal.pku.edu.cn/\">门户</a></li>\n" +
                "\t\t\t\t\t<li id=\"its\"><a href=\"http://its.pku.edu.cn/\">网络</a></li>\n" +
                "                    <li id=\"its\"><a href=\"http://mail.pku.edu.cn/\">邮箱</a></li>\n" +
                "                    <li id=\"its\"><a href=\"http://bbs.pku.edu.cn/\">BBS</a></li>\n" +
                "\t\t\t\t\t<li id=\"lib\"><a href=\"http://www.lib.pku.edu.cn/portal/\">图书馆</a></li>\n" +
                "\t\t\t\t\t<li id=\"bbs\"><a href=\"http://www.bjmu.edu.cn/\">医学部</a></li>\n" +
                "\t\t\t\t\t<li id=\"leadermailbox\" ><a href=\"../leadermailbox/index.htm\">领导信箱</a></li>                     \n" +
                "                     <li id=\"en\"><a href=\"http://english.pku.edu.cn/\">English</a></li>\n" +
                "                     <li id=\"form_Search_itme\">\n" +
                "\t\t\t\t\t<a id=\"form_Search_a\" href=\"#\" class=\"glyphicon glyphicon-search btn searchlogo\" style=\"padding-top:3px;font-size:12px;\"></a>\n" +
                "\t\t\t\t\t\t<div class=\"searchDIV\">\n" +
                "\t\t\t\t\t\t\t<form id=\"bing_Search\" name=\"bing_Search\" method=\"get\" action=\"http://cn.bing.com/search\" target=\"_blank\" onsubmit=\"return false;\">\n" +
                "\t\t\t\t\t\t\t\t<input id=\"form_Searchword\" name=\"form_Searchword1\" class=\"searchinp\" onFocus=\"\" onBlur=\"\" type=\"text\" placeholder=\"Search...\" onKeyDown=\"doSearch(event,1)\">\n" +
                "<input id=\"q\" name=\"q\" type=\"hidden\">\n" +
                "\t\t\t\t\t\t\t\t<input id=\"ensearch\" name=\"ensearch\" value=\"1\" type=\"hidden\">\n" +
                "\t\t\t\t\t\t\t\t<a id=\"form_SearchGo\" href=\"javascript: bingsearch(1);\" class=\"searchGO\" title=\"Search\" style=\"color:#fff;display:inline;\">go</a>\n" +
                "\t\t\t\t\t\t\t</form>\n" +
                "\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t</li>\n" +
                "\t\t\t\t</ul>\n" +
                "\t\t\t</section>\n" +
                "            \n" +
                "            <!-- search -->\n" +
                "\t\t\t<section class=\"clearfix\">\n" +
                "\t\t\t\n" +
                "\t\t\t</section>\n" +
                "\n" +
                "<!-- for mobile [begin] -->\n" +
                "\t\t\t<section id=\"mobileNav\" class=\"onlymobileshow clearfix\">\n" +
                "\t\t\t<div style=\"background-color:#fff;clear:both;padding-left:20px;padding-bottom:7px;line-height:30px;font-size:16px;\">    <!--20170905 add   -->\n" +
                "            <table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"show-lg\">\n" +
                "\t\t\t\t<tr>\n" +
                "                     <td><a href=\"http://www.lib.pku.edu.cn/portal/\">图书馆</a></td>\n" +
                "                     <td><a href=\"http://www.bjmu.edu.cn/\">医学部</a></td>\n" +
                "                     <td><a href=\"../leadermailbox/index.htm\">领导信箱</a></td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t<tr>   \n" +
                "                     <td><a href=\"http://english.pku.edu.cn/\">English</a></td>\n" +
                "                     <td>&nbsp;</td>\n" +
                "                     <td>&nbsp;</td>\t\t\t\t \n" +
                "\t\t\t\t</tr>\t\t\t\t\t\t\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t<td colspan=\"2\"><form id=\"bing_Search2\" name=\"bing_Search2\" method=\"get\" action=\"#\" target=\"_blank\"  onsubmit=\"return false;\">\n" +
                "\t\t\t\t\t\t<input id=\"form_Searchword\" name=\"form_Searchword2\" style=\"opacity:0.8;background-color:#fff;border:1px solid #C4C4C4;width:70%;margin-right:3px;margin-top:3px;\" onFocus=\"\" onBlur=\"\" type=\"text\"  onKeyDown=\"doSearch(event,2)\">\n" +
                "\t\t\t\t\t\t<a href=\"javascript: bingsearch(2);\" class=\"glyphicon glyphicon-search btn\" style=\"line-height:22px;font-size:12px\" onClick=\"\" title=\"Search\"></a>\n" +
                "\t\t\t\t\t\t</form>\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t</table>\n" +
                "            <!--20170905 end  -->\n" +
                "\t\t\t\t<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"show-md\">\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t <td width=\"35%\"><a href=\"index.htm\">北大概况</a></td>\n" +
                "\t\t\t\t\t <td width=\"30%\"><a href=\"../students/index.htm\">学生</a></td>\n" +
                "\t\t\t\t\t <td width=\"33%\"><a href=\"https://portal.pku.edu.cn/\">门户</a></td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t <td><a href=\"../admissions/index.htm\">招生与资助</a></td>\n" +
                "\t\t\t\t\t <td><a href=\"../facultystaff/index.htm\">教职工</a></td>\n" +
                "\t\t\t\t\t <td><a href=\"https://its.pku.edu.cn/\">网络</a></td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t<td><a href=\"../academics/index.htm\">学部与院系</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"http://www.pku.org.cn/\">校友</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"http://bbs.pku.edu.cn/\">BBS</a></td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t<td><a href=\"../education/index.htm\">教育教学</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"../parents/index.htm\">家长</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"http://www.lib.pku.edu.cn/portal/\">图书馆</a></td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t<td><a href=\"../research/index.htm\">科学研究</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"../visitors/index.htm\">访客</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"http://www.bjmu.edu.cn/\">医学部</a></td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t<td><a href=\"../collaboration/index.htm\">合作交流</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"http://hr.pku.edu.cn/rczp/js/\">招聘</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"../leadermailbox/index.htm\">领导信箱</a></td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t\t<tr>\n" +
                "\t\t\t\t\t<td><a href=\"../campuslife/index.htm\">校园生活</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"http://www.pkuef.org/pkuef/\">捐赠</a></td>\n" +
                "\t\t\t\t\t<td><a href=\"http://english.pku.edu.cn/\">English</a></td>\n" +
                "\t\t\t\t</tr>   \t\t\t\t\t\t                  \n" +
                "                <tr>\n" +
                "\t\t\t\t\t<td colspan=\"2\"><form id=\"bing_Search3\" name=\"bing_Search3\" method=\"get\" action=\"#\" target=\"_blank\" onsubmit=\"return false;\">\n" +
                "\t\t\t\t\t\t<input name=\"form_Searchword3\"  id=\"form_Searchword\" style=\"opacity:0.8;background-color:#fff;border:1px solid #C4C4C4;width:70%;margin-right:3px;margin-top:3px;\" onFocus=\"\" onBlur=\"\" type=\"text\"  onKeyDown=\"doSearch(event,3)\">\n" +
                "\t\t\t\t\t\t<a href=\"javascript: bingsearch(3);\" class=\"glyphicon glyphicon-search btn\" style=\"line-height:22px;font-size:12px\" onClick=\"\" title=\"Search\"></a>\n" +
                "\t\t\t\t\t</form>\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t<td></td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t</table>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t</section>\n" +
                "\t\t\t<!--  for mobile [end] -->\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t\t</section>\n" +
                "\n" +
                "\t\t<section class=\"topWrap clearfix\">\n" +
                "\n" +
                "\t\t\t<section class=\"mainWrap\"><a class=\"logo\" href=\"../index.htm\" title=\"北京大学首页\"><img src=\"../img/pkulogo_white.png\"></a></section>\n" +
                "\n" +
                "\t\t\t<section class=\"nav yahei clearfix noMobileShow\" style=\"clear:both;\">\n" +
                "\n" +
                "\t\t\t\t<section class=\"menu\" id=\"smenu\"><a class=\"thuicon-menu menuicon\"></a>导航</section>\n" +
                "\n" +
                "\t\t\t\t\t<ul id=\"nav\">\n" +
                "\t\t\t\t\t\t<li class=\"nav_first\"><a id=\"intro\"  class=\"current\"    href=\"index.htm\">北大概况</a>\n" +
                "\t\t\t\t\t\t\t<div class=\"minfoWrap\">\n" +
                "\t\t\t\t\t\t\t\t<div class=\"minfoWrap_inner\">\n" +
                "\t\t\t\t\t\t\t\t\t<div class=\"fl\"><img src=\"../img/channel_about.jpg\" border=\"0\" /></div>\n" +
                "\t\t\t\t\t\t\t\t\t<p class=\"fl nav03 slogan\" style=\"margin-left:40px; margin-right:10px\">北大是常为新的，<br>改进的运动的先锋，<br>要使中国向着好的，<br>往上的道路走。</p>\n" +
                "\t\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl ml30\">\n" +
                "\t\t\t\t\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\t\t\t\t<li id=\"intro_about\"><a href=\"bdjj/index.htm\">北大简介</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li id=\"intro_leaders\"><a href=\"xrld/index.htm\">现任领导</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li id=\"intro_formerleaders\"><a href=\"lrld/index.htm\">历任领导</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li id=\"intro_organization\"><a href=\"zzjg/index.htm\">组织机构</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li id=\"intro_celebrities\"><a href=\"lsmr/index.htm\">历史名人</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li id=\"intro_internals\"><a href=\"http://xxgk.pku.edu.cn/\">信息公开</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li id=\"intro_greencampus\"><a href=\"http://green.pku.edu.cn/\">绿色校园</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t<li><a id=\"admissions\"   href=\"../admissions/index.htm\">招生与资助</a>\n" +
                "\t\t\t\t\t\t\t<div class=\"minfoWrap\">\n" +
                "\t\t\t\t\t\t\t\t<div class=\"minfoWrap_inner\">\n" +
                "\t\t\t\t\t\t\t\t\t<div class=\"fl\"><img src=\"../img/channel_admissions.jpg\" border=\"0\" /></div>\n" +
                "\t\t\t\t\t\t\t\t\t<p class=\"fl nav03 slogan\" style=\"margin-left:40px; margin-right:10px\">燕园画卷美不胜收，<br>恰似你将要留在这里的青春年华。<br><br>北京大学欢迎你！</p>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 ml30 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.gotopku.cn/\">本科生</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"https://admission.pku.edu.cn\">研究生</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.isd.pku.edu.cn/\">留学生</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.oce.pku.edu.cn/\">继续教育</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 ml30 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://gotopku.cn/programa/page/16.html\">本科生奖助</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://grs.pku.edu.cn/jzgz/dtxx1/\">研究生奖助</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.sfao.pku.edu.cn/\">学生资助中心</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://summer.pku.edu.cn/\">暑期学校</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.oir.pku.edu.cn/summerschool/\">国际暑期学校</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t<li><a    id=\"academics\" href=\"../academics/index.htm\">学部与院系</a>\n" +
                "\t\t\t\t\t\t\t<div class=\"minfoWrap\">\n" +
                "\t\t\t\t\t\t\t\t<div class=\"minfoWrap_inner\">\n" +
                "\t\t\t\t\t\t\t\t\t<div class=\"fl\"><img src=\"../img/channel_academic.jpg\" border=\"0\" /></div>\n" +
                "\t\t\t\t\t\t\t\t\t<p class=\"fl nav03 slogan\" style=\"margin-left:40px; margin-right:10px\">勤奋、严谨、求实、创新。</p>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 ml30 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../academics/index.htm#1\">理学部</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../academics/index.htm#2\">信息与工程科学部</a></li>\n" +
                "                                                                                <li><a href=\"../academics/index.htm#7\">深圳研究生院</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../academics/index.htm#3\">人文学部</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../academics/index.htm#4\">社会科学学部</a></li>\n" +
                "                                                                                <li><a href=\"../academics/index.htm#4\">经济与管理学部</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t        <li><a href=\"../academics/index.htm#5\">医学部</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../academics/index.htm#6\">跨学科类</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\n" +
                "\t\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t<li><a  id=\"education\" href=\"../education/index.htm\">教育教学</a>\n" +
                "\t\t\t\t\t\t\t<div class=\"minfoWrap\">\n" +
                "\t\t\t\t\t\t\t\t<div class=\"minfoWrap_inner\">\n" +
                "\t\t\t\t\t\t\t\t\t<div class=\"fl\"><img src=\"../img/channel_education.jpg\" border=\"0\" /></div>\n" +
                "\t\t\t\t\t\t\t\t\t<p class=\"fl nav03 slogan\" style=\"margin-left:40px; margin-right:10px\">思想自由、兼容并包。<br><br>教育应指导社会，而非追逐社会。</p>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 ml30 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../education/szdw/index.htm\">师资队伍</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.dean.pku.edu.cn/\">本科生教育</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://grs.pku.edu.cn/\">研究生教育</a></li>\n" +
                "                                                                                <li><a href=\"http://www.isd.pku.edu.cn/\">留学生教育</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.oce.pku.edu.cn/\">继续教育</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../education/kcsz/index.htm\">课程设置</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.oir.pku.edu.cn/Category_35/Index.aspx\">海外学习</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://dean.pku.edu.cn/tutorialclasses/\">小班课教学</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://dean.pku.edu.cn/englishcourses/\">本科全英文授课</a></li>\n" +
                "                                                                                <li><a href=\"http://dean.pku.edu.cn/pkudean/course/kcb.php?ll=1\">课表查询</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://moocs.pku.edu.cn/\">慕课</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://course.pku.edu.cn/\">教学网</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://opencourse.pku.edu.cn/\">公开课</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../education/zsyz/index.htm\">证书验证</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t<li><a   id=\"research\" href=\"../research/index.htm\">科学研究</a>\n" +
                "\t\t\t\t\t\t\t<div class=\"minfoWrap\">\n" +
                "\t\t\t\t\t\t\t\t<div class=\"minfoWrap_inner\">\n" +
                "\t\t\t\t\t\t\t\t\t<div class=\"fl\"><img src=\"../img/channel_research.jpg\" border=\"0\" /></div>\n" +
                "\t\t\t\t\t\t\t\t\t<p class=\"fl nav03 slogan\" style=\"margin-left:40px; margin-right:10px\">博学之，审问之，<br>慎思之，明辨之，笃行之。</p>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 ml30 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../research/index.htm\">自然科学</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../research/rwsk/index.htm\">人文社科</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../research/cxy/index.htm\">产学研</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../research/kycg/index.htm\">科研成果</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../research/kyjg/index.htm\">科研机构</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../research/xzqk/index.htm\">学术期刊</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../research/glbm/index.htm\">管理部门</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t<li><a id=\"collaboration\"   href=\"../collaboration/index.htm\">合作交流</a>\n" +
                "\t\t\t\t\t\t\t<div class=\"minfoWrap\">\n" +
                "\t\t\t\t\t\t\t\t<div class=\"minfoWrap_inner\">\n" +
                "\t\t\t\t\t\t\t\t\t<div class=\"fl\"><img src=\"../img/channel_collaboration.jpg\" border=\"0\" /></div>\n" +
                "\t\t\t\t\t\t\t\t\t<p class=\"fl nav03 slogan\" style=\"margin-left:40px; margin-right:10px\">志于道，据于德，<br>依于仁，游于艺。<br></p>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 ml30 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../collaboration/index.htm\">合作概览</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://gnhz.pku.edu.cn/\">内地合作</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.oir.pku.edu.cn/\">国际交流</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://hmt.pku.edu.cn/\">港澳台交流</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.hanban.pku.edu.cn/\">孔子学院</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t\t<li class=\"nav_last\"><a    id=\"campuslife\" href=\"../campuslife/index.htm\">校园生活</a>\n" +
                "\t\t\t\t\t\t\t<div class=\"minfoWrap\">\n" +
                "\t\t\t\t\t\t\t\t<div class=\"minfoWrap_inner\">\n" +
                "\t\t\t\t\t\t\t\t\t<div class=\"fl\"><img src=\"../img/channel_life.jpg\" border=\"0\" /></div>\n" +
                "\t\t\t\t\t\t\t\t\t<p class=\"fl nav03 slogan\" style=\"margin-left:40px; margin-right:10px\">秋冬春夏，<br>伴随着四时的运行，<br>青春和燕园融为一体，<br>北大成为永恒。</p>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 ml30 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../campuslife/yzywh/index.htm\">艺术与文化</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../campuslife/tyyjk/index.htm\">体育与健康</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../campuslife/xsst/index.htm\">学生社团</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.gh.pku.edu.cn/plus/list.php?tid=214\n" +
                "\">教工社团</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../campuslife/zygy/index.htm\">志愿公益</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../campuslife/xsda/index.htm\">校史档案</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://lecture.pku.edu.cn/\">会议讲座</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"http://www.pku-hall.com/\">电影演出</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\t\t\t\t\t\t\t\t\t<ul class=\"nav01 fl\">\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"zzjg/glfw/index.htm\">管理服务</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../campuslife/hqfw/index.htm\">后勤服务</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../campuslife/ggfw/index.htm\">观光访问</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t\t<li><a href=\"../campuslife/xl/index.htm\">校历</a></li>\n" +
                "\t\t\t\t\t\t\t\t\t</ul>\n" +
                "\n" +
                "\t\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t</li>\n" +
                "\t\t\t\t\t</ul>\n" +
                "\n" +
                "\t\t\t</section>\n" +
                "\n" +
                "\t\t</section>\n" +
                "\n" +
                "\t</section>\n" +
                "\n" +
                "</header>\n" +
                "<!--  pku_header_section_end   -->\n" +
                "<!--\n" +
                "<div id=\"header_placeholder\"></div>\n" +
                "-->\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "\tvar _filename_ = location.pathname.match(/[^\\/]+$/);\n" +
                "\n" +
                " \tif (_filename_ != null && _filename_.length == 1)\n" +
                "\t{\n" +
                "\t\tvar _id_ = _filename_[0].match(/^[^_\\.]+/);\n" +
                "\n" +
                "\t\tif (_id_ != null && _id_.length > 0)\n" +
                "\t\t{\n" +
                "\t\t\t$(\"#\" + _id_).addClass(\"current\");\n" +
                "\t\t}\n" +
                "\t}\n" +
                "$(function(){\n" +
                "\t$(\".ssubNav1\").toggle(function(){\n" +
                "\t\t$(\"#mobileNav\").slideDown(300);\n" +
                "\t},function(){\n" +
                "\t\t$(\"#mobileNav\").slideUp(300);\n" +
                "\t})\n" +
                "\t$(\".searchlogo\").click(function(event){\n" +
                "\t\tevent.stopPropagation();\n" +
                "\t\t$('.searchDIV').slideToggle('fast');\n" +
                "\t});\n" +
                "});\n" +
                "\n" +
                "/*-- zhangyang 2018.03.21 -[begin]--*/\n" +
                "function bingsearch(num){\n" +
                "if($(\"input[name='form_Searchword\"+num+\"']\").val()==\"\"){return;}\n" +
                "$(\"#q\").val($(\"input[name='form_Searchword\"+num+\"']\").val()+\"+site:pku.edu.cn\");\n" +
                "document.forms[\"bing_Search\"].submit();\n" +
                "$(\"input[name='form_Searchword\"+num+\"']\").val(\"\");\n" +
                "$(\"#q\").val(\"\");}\n" +
                "function doSearch(keypressed,formnum){var key;\n" +
                "if(document.all){key=window.event.keyCode;\n" +
                "}else{key=keypressed.which;};\n" +
                "if(key==13||key==10){bingsearch(formnum);}}\n" +
                "/*-- zhangyang 2018.03.21 -[end]--*/\n" +
                "\n" +
                "$(\".header .mainWrap a:first-child\").hover(function(){\n" +
                "$(this).children(\"img\").attr(\"src\",\"../img/pkulogo_grey.png\");\n" +
                "},function(){$(this).children(\"img\").attr(\"src\",\"../img/pkulogo_white.png\");});\n" +
                "\n" +
                "</script>\n" +
                "\n" +
                "<article class=\"content mb50\">\n" +
                "\t<section class=\"\">\n" +
                "\t\t<div class=\"bouti-tit\">\n" +
                "\t\t\t<div class=\"bouti-link bouti-link-intro\">\n" +
                "\t\t\t\t<div class=\"boutiCon\">\n" +
                "\t\t\t\t\t<h3>北大概况</h3>\n" +
                "\t\t\t\t</div>\n" +
                "\t\t\t\t<div class=\"boutiConBg\"></div>\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t</section>\n" +
                "\t<section class=\"mainWrap relative\">\n" +
                "\t\t<header>\n" +
                "\t\t\t<section class=\"mobSecNav\"><img id=\"subNavlogo\" src=\"../images/mnav2.png\" style=\"padding-bottom:6px;\"></section>\n" +
                "\t\t\t<section class=\"secNav\" id=\"example-one\">\n" +
                "\t\t\t\t<ul>\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\t<li class=\"cur\" id=\"intro_about\"><a href=\"bdjj/index.htm\">北大简介</a></li>\n" +
                "\t\t\t\t\t<li id=\"intro_leaders\"><a href=\"xrld/index.htm\">现任领导</a></li>\n" +
                "\t\t\t\t\t<li id=\"intro_formerleaders\"><a href=\"lrld/index.htm\">历任领导</a></li>\n" +
                "\t\t\t\t\t<li id=\"intro_organization\"><a href=\"zzjg/zzjgyxsz/index.htm\">组织机构</a></li>\n" +
                "\t\t\t\t\t<li id=\"intro_celebrities\"><a href=\"lsmr/index.htm\">历史名人</a></li>\n" +
                "\t\t\t\t\t<li id=\"intro_internals\"><a href=\"http://xxgk.pku.edu.cn/\">信息公开</a></li>\n" +
                "\t\t\t\t\t<li id=\"intro_greencampus\"><a href=\"http://green.pku.edu.cn/\">绿色校园</a></li>\n" +
                "\t\t\t\t</ul>\n" +
                "\t\t\t</section>\n" +
                "\t\t</header>\n" +
                "\t\t<div class=\"detailContent clearfix\">\n" +
                "\t\t\t<div class=\"column_5\">\n" +
                "\t\t\t\t<article class=\"mainContent\">\n" +
                "\t\t\t\t\t<div class=\"introTit clearfix\">\n" +
                "\t\t\t\t\t\t<div class=\"introTitImg\"></div>\n" +
                "\t\t\t\t\t\t<h2>北大简介</h2>\n" +
                "\t\t\t\t\t</div>\n" +
                "\t\t\t\t\t<section class=\"article_cn\"> \n" +
                "<p>北京大学创办于1898年，初名京师大学堂，是中国第一所国立综合性大学，也是当时中国最高教育行政机关。辛亥革命后，于1912年改为现名。</p>\n" +
                "<p>作为新文化运动的中心和“五四”运动的策源地，作为中国最早传播马克思主义和民主科学思想的发祥地，作为中国共产党最早的活动基地，北京大学为民族的振兴和解放、国家的建设和发展、社会的文明和进步做出了不可替代的贡献，在中国走向现代化的进程中起到了重要的先锋作用。爱国、进步、民主、科学的传统精神和勤奋、严谨、求实、创新的学风在这里生生不息、代代相传。</p>\n" +
                "<p>1917年，著名教育家蔡元培出任北京大学校长，他“循思想自由原则，取兼容并包主义”，对北京大学进行了卓有成效的改革，促进了思想解放和学术繁荣。陈独秀、李大钊、毛泽东以及鲁迅、胡适等一批杰出人才都曾在北京大学任职或任教。</p>\n" +
                "<p>1937年卢沟桥事变后，北京大学与清华大学、南开大学南迁长沙，共同组成长沙临时大学。不久，临时大学又迁到昆明，改称国立西南联合大学。抗日战争胜利后，北京大学于1946年10月在北平复学。 </p>\n" +
                "<p>中华人民共和国成立后，全国高校于1952年进行院系调整，北京大学成为一所以文理基础教学和研究为主的综合性大学，为国家培养了大批人才。据不完全统计，北京大学的校友和教师有400多位两院院士，中国人文社科界有影响的人士相当多也出自北京大学。</p>\n" +
                "<p>改革开放以来，北京大学进入了一个前所未有的大发展、大建设的新时期，并成为国家“211工程”重点建设的两所大学之一。</p>\n" +
                "<p>1998年5月4日，北京大学百年校庆之际，国家主席江泽民在庆祝北京大学建校一百周年大会上发表讲话，发出了“为了实现现代化，我国要有若干所具有世界先进水平的一流大学”的号召。在国家的支持下，北京大学适时启动“创建世界一流大学计划”，从此，北京大学的历史翻开了新的一页。</p>\n" +
                "<p>2000年4月3日，北京大学与原北京医科大学合并，组建了新的北京大学。原北京医科大学的前身是国立北京医学专门学校，创建于1912年10月26日。20世纪三、四十年代，学校一度名为北平大学医学院，并于1946年7月并入北京大学。1952年在全国高校院系调整中，北京大学医学院脱离北京大学，独立为北京医学院。1985年更名为北京医科大学，1996年成为国家首批“211工程”重点支持的医科大学。两校合并进一步拓宽了北京大学的学科结构，为促进医学与人文社会科学及理科的结合，改革医学教育奠定了基础。</p>\n" +
                "<p>近年来，在“211工程”和“985工程”的支持下，北京大学进入了一个新的历史发展阶段，在学科建设、人才培养、师资队伍建设、教学科研等各方面都取得了显著成绩，为将北大建设成为世界一流大学奠定了坚实的基础。今天的北京大学已经成为国家培养高素质、创造性人才的摇篮、科学研究的前沿和知识创新的重要基地和国际交流的重要桥梁和窗口。 \n" +
                "<p>现任校党委书记郝平教授、校长林建华教授。 \n" +
                "\t\t\t\t\t</section>\n" +
                "\t\t\t\t</article>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<div class=\"column_1\">\n" +
                "\t\t\t\t<div class=\"rightBg\"></div>\n" +
                "\t\t\t\t<!--img src=\"../img/pkuplaque.png\" style=\"width:100%\" /-->\n" +
                "\t\t\t</div>\n" +
                "\t\t</div>\n" +
                "\t</section>\n" +
                "</article>\n" +
                "\n" +
                "\n" +
                "<footer class=\"footer\">\n" +
                "        <div id=\"wechats\" class=\"img_list\">\n" +
                "                <div><img src=\"../img/pkuweixin.png\"/><span>北京大学官方微信</span></div>\n" +
                "                <div><img src=\"../img/pkuyinxin.png\"/><span>北京大学招生办</span></div>\n" +
                "                <div><img src=\"../img/pkualumni.png\"/><span>北京大学校友会</span></div>\n" +
                "                <div><img src=\"../img/pkuinfosvc.png\" /><span>北京大学信息服务</span></div>\n" +
                "                <div><img src=\"../img/pkuxinqingnian.png\"/><span>北大新青年</span></div>\n" +
                "                <div><img src=\"../img/pkubbs.png\"/><span>北大未名BBS</span></div>\n" +
                "        </div>\n" +
                "    <section class=\"fot_wap\">\n" +
                "        <section class=\"fot_left\">            \n" +
                "            <section class=\"sf\">\n" +
                "                <a href=\"index.htm\">北大概况</a>\n" +
                "                <a href=\"../admissions/index.htm\">招生资助</a>\n" +
                "                <a href=\"../academics/index.htm\">院系设置</a>\n" +
                "                <a href=\"../education/index.htm\">教育教学</a>\n" +
                "                <a href=\"../research/index.htm\">科学研究</a>\n" +
                "                <a href=\"../collaboration/index.htm\">合作交流</a>\n" +
                "                <a href=\"../campuslife/index.htm\">校园生活</a>\n" +
                "                <a href=\"../campuslife/ggfw/index.htm\">观光访问</a>\n" +
                "            </section>\n" +
                "        </section>\n" +
                "        <section class=\"fot_cen\">\n" +
                "            <a href=\"../index.htm\"><img src=\"../img/pkulogo_white.png\" /> </a>\n" +
                "        </section>\n" +
                "      <section class=\"fot_right\">\n" +
                "        \t<div class=\"bdsharebuttonbox pku_share2\" data-tag=\"share_2\">\n" +
                "\t        \t<a style=\"padding-right:18px;background:url(../images/share01.png) no-repeat;\"  href=\"http://weibo.com/PKU\" target=\"_blank\"></a> \n" +
                "               \t<a  style=\"padding-right:18px;background:url(../images/share02.png) no-repeat;\"   href=\"javascript:void(0);\" id=\"wechat\"></a> \n" +
                "                <a class=\"bds_isohu\" style=\"padding-right:18px;background:url(../images/share08.png) no-repeat;\" data-cmd=\"isohu\" href=\"#\"></a>\n" +
                "\t\t        <a class=\"bds_sqq\" data-cmd=\"sqq\" style=\"background:url(../images/share03.png) no-repeat;\" href=\"#\"></a> \n" +
                "                <a class=\"bds_tqq\" style=\"padding-right:18px;background:url(../images/share04.png) no-repeat;\" data-cmd=\"tqq\" href=\"#\"></a> \n" +
                "                <a class=\"bds_douban\" style=\"padding-right:18px;background:url(../images/share05.png) no-repeat;\" data-cmd=\"douban\" href=\"#\"></a>\n" +
                "                <a class=\"bds_renren\" style=\"padding-right:18px;background:url(../images/share06.png) no-repeat;\" data-cmd=\"renren\" href=\"#\"></a>\n" +
                "                <a class=\"bds_tqf\" data-cmd=\"tqf\" style=\"background:url(../images/share07.png) no-repeat;\" href=\"#\"></a> \n" +
                "        \t</div>\n" +
                "\t</section>\n" +
                "    </section>\n" +
                "    <section class=\"fot_wap6\">\n" +
                "        <span><a href=\"../index.htm\"><img src=\"../img/pkulogo_white.png\" /> </a></span>\n" +
                "   \t<div class=\"bdsharebuttonbox pku_share\" data-tag=\"share_3\">\n" +
                "              <a style=\"margin-left:20px;background:url(../images/share01.png) no-repeat;\" href=\"http://weibo.com/PKU\" target=\"_blank\"></a>\n" +
                "              <a style=\"margin-left:20px;background:url(../images/share02.png) no-repeat;\" href=\"javascript:void(0);\" id=\"wechat2\"></a>\n" +
                "              <a class=\"bds_isohu\" style=\"margin-left:20px;background:url(../images/share08.png) no-repeat;\" data-cmd=\"isohu\" href=\"#\"></a>\n" +
                "              <a class=\"bds_sqq\" style=\"margin-left:20px;background:url(../images/share03.png) no-repeat;\" data-cmd=\"sqq\" href=\"#\"></a>\n" +
                "              <a class=\"bds_tqq\" style=\"margin-left:20px;background:url(../images/share04.png) no-repeat;\" data-cmd=\"tqq\" href=\"#\"></a>\n" +
                "              <a class=\"bds_douban\" style=\"margin-left:20px;background:url(../images/share05.png) no-repeat;\" data-cmd=\"douban\" href=\"#\"></a>\n" +
                "              <a class=\"bds_renren\" style=\"margin-left:20px;background:url(../images/share06.png) no-repeat;\" data-cmd=\"renren\" href=\"#\"></a>\n" +
                "              <a class=\"bds_tqf\" style=\"margin-left:20px;background:url(../images/share07.png) no-repeat;\" data-cmd=\"tqf\" href=\"#\"></a>             \n" +
                "       </div> \n" +
                "   </section>\n" +
                "    <section class=\"fot_bot clear_f\">\n" +
                "        <section class=\"bq1\">\n" +
                "            版权所有&copy;北京大学<span>|</span>地址：北京市海淀区颐和园路5号<span>|</span>邮编：100871<span>|</span>邮箱：webmaster@pku.edu.cn<span>|</span>京ICP备05065075号-1<span>|</span>京公网安备 110402430047 号\n" +
                "        </section>\n" +
                "        <section class=\"bq2\">\n" +
                "            版权所有&copy;北京大学<span>|</span>地址：北京市海淀区颐和园路5号<span>|</span>邮编：100871<br />邮箱：webmaster@pku.edu.cn<span>|</span>京ICP备05065075号-1<span>|</span>京公网安备 110402430047 号\n" +
                "        </section>\n" +
                "        <section class=\"bq3\">\n" +
                "            <div id=\"bq3_left\"><span>版权所有&copy;北京大学</span><span>邮编：100871</span><span>京ICP备05065075号-1</span></div>\n" +
                "            <div id=\"bq3_mid\"><span>|</span><span>|</span><span>|</span></div>\n" +
                "            <div id=\"bq3_right\"><span>地址：北京市海淀区颐和园路5号</span><span>邮箱：webmaster@pku.edu.cn</span><span>京公网安备 110402430047 号</span></div>\n" +
                "        </section>\n" +
                "    </section>\n" +
                "<script>\n" +
                "\twindow._bd_share_config={\n" +
                "\t\t\"common\":{\n" +
                "\t\t\t\"bdSnsKey\":{},\n" +
                "\t\t\t\"bdText\":\"\",\n" +
                "\t\t\t\"bdMini\":\"2\",\n" +
                "\t\t\t\"bdMiniList\":false,\n" +
                "\t\t\t\"bdPic\":\"\",\n" +
                "\t\t\t\"bdStyle\":\"0\",\n" +
                "\t\t\t\"bdSize\":\"32\"\n" +
                "\t\t},\"share\":{}\n" +
                "\t};\n" +
                "\twith(document)0[(getElementsByTagName('head')[0]||body).appendChild(createElement('script')).src='http://bdimg.share.baidu.com/static/api/js/share.js?v=89860593.js?cdnversion='+~(-new Date()/36e5)];\n" +
                "        $(\"a#wechat\").hover(function(){\n" +
                "            $(\"div#wechats\").css(\"top\",\"105px\");\n" +
                "            $(\"div#wechats\").fadeIn();\n" +
                "        },function(){\n" +
                "            $(\"div#wechats\").fadeOut();\n" +
                "        });\n" +
                "        $(\"a#wechat2\").hover(function(){\n" +
                "            $(\"div#wechats\").css(\"top\",\"0px\");\n" +
                "            $(\"div#wechats\").fadeIn();\n" +
                "        },function(){\n" +
                "            $(\"div#wechats\").fadeOut();\n" +
                "        });\n" +
                "</script>\n" +
                "</footer>\n" +
                "</body></html>\n" +
                "<script type=\"text/javascript\">\n" +
                "$(function(){\n" +
                "\tshowRandomBigImage();\n" +
                "\tshowRandomSmImage();\n" +
                "\tshowRandomSmImageRight();\n" +
                "})\n" +
                "function showRandomBigImage(){\n" +
                "\tvar idx = Math.ceil(Math.random()*14);\n" +
                "\t$(\".bouti-link-intro\").css(\"background-image\",\"url(../img/img_about1\"+idx+\".jpg)\");\n" +
                "}\n" +
                "function showRandomSmImage(){\n" +
                "\tvar idx = Math.ceil(Math.random()*9);\n" +
                "\t$(\".introTitImg\").css(\"background-image\",\"url(../img/img_small\"+idx+\".jpg)\");\n" +
                "}\n" +
                "function showRandomSmImageRight(){\n" +
                "\tvar idx = Math.ceil(Math.random()*17);\n" +
                "\t$(\".rightBg\").css(\"background-image\",\"url(../img/img_about2\"+idx+\".jpg)\");\n" +
                "}\n" +
                "</script>\n" +
                "</body></html>";

        Text text = new Text(htmlString);
        // System.out.println(text.getExtractedHTML());
        System.out.println(text.getWordSegmentedExtractedHTML());


        // text.wordSegmentExtractedPlainText("`");

        // System.out.println(text.getExtractedPlainText());

        // for (POS pos : text.getPosWordsMap().keySet()) {
        //     System.out.println(pos);
        //     System.out.println(text.getPosWordsMap().get(pos).toString());
        // }

        for (NER ner : text.getNerWordsMap().keySet()) {
            System.out.println(ner);
            System.out.println(text.getNerWordsMap().get(ner).toString());
        }

        // System.out.println(text.getExtractedPlainText());
        // System.out.println(text.getExtractedHTML());
        // org.jsoup.nodes.Document document = Jsoup.parse(text.getExtractedHTML());
        // Elements elements = document.body().getAllElements();

        // for (Element element : elements) {
        //     List <TextNode> textNodeList = element.textNodes();
        //     for (TextNode textNode : textNodeList) {
        //         // System.out.println(textNode.text());
        //         String original = textNode.text();
        //         // DataNode dataNode = new DataNode(NLPServices.wordSegment(original, "`"));
        //         // textNode.replaceWith(NLPServices.wordSegment(textNode.text(), "`"));
        //     }
        //
        // }
    }

}
