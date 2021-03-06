package services.nlp;

/**
 * Use java to build a http connection and requesst the ltp-cloud service for some simple
 * Natural Language Processing analysis,the results are in plain formats
 */

import services.spellcheck.SpellingCorrector;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

public class NLPServices {
    private final static String api_key = "L188H9h095NEaKVUnnQz2XnBlzQVqTLNckqVRn7I";
    private static String format = "plain";

    private static Map <String, POS> posMap = new HashMap <String, POS>() {{
        put("a", POS.ADJECTIVE);
        put("b", POS.OTHER_NOUN_MODIFIER);
        put("c", POS.CONJUNCTION);
        put("d", POS.ADVERB);
        put("e", POS.EXCLAMATION);
        put("g", POS.MORPHEME);
        put("h", POS.PREFIX);
        put("i", POS.IDIOM);
        put("j", POS.ABBREVIATION);
        put("k", POS.SUFFIX);
        put("m", POS.NUMBER);
        put("n", POS.GENERAL_NOUN);
        put("nd", POS.DIRECTION_NOUN);
        put("nh", POS.PERSON_NAME);
        put("ni", POS.ORGANIZATION_NAME);
        put("nl", POS.LOCATION_NOUN);
        put("ns", POS.GEOGRAPHICAL_NAME);
        put("nt", POS.TEMPORAL_NOUN);
        put("nz", POS.OTHER_PROPER_NOUN);
        put("o", POS.ONOMATOPOEIA);
        put("p", POS.PREPOSITION);
        put("q", POS.QUANTITY);
        put("r", POS.PRONOUN);
        put("u", POS.AUXILIARY);
        put("v", POS.VERB);
        put("wp", POS.PUNCTUATION);
        put("ws", POS.FOREIGN_WORDS);
        put("x", POS.NON_LEXEME);
    }};

    private static Map <String, NER> nerMap = new HashMap <String, NER>() {{
        put("Ni", NER.INSTITUTION_NAME);
        put("Nh", NER.PERSON_NAME);
        put("Ns", NER.PLACE_NAME);
    }};


    public static Set <String> stopWordSet = new HashSet <>();

    static {
        ClassLoader classLoader = NLPServices.class.getClassLoader();
        File file = new File(classLoader.getResource("nlp/stopwords.txt").getFile());
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String temp;
            while ((temp = reader.readLine()) != null) {
                stopWordSet.add(temp);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * For a given sentence,	return the spilt result
     *
     * @param sentence an	array of words  £¬a separator
     * @return an list of String show the words spilt by the separator
     */
    public static List <String> wordSegment(String[] sentence, String separator) throws IOException {
        String pattern = "ws";
        String text = sentence[0];

        for (int ord = 1; ord < sentence.length; ord++) {
            text += sentence[ord];
        }

        text = URLEncoder.encode(text, "utf-8");
        URL url = new URL("https://api.ltp-cloud.com/analysis/?"
                + "api_key=" + api_key + "&"
                + "text=" + text + "&"
                + "format=" + format + "&"
                + "pattern=" + pattern + "&"
                + "only_ner=true");
        URLConnection conn = url.openConnection();
        conn.connect();

        BufferedReader innet = new BufferedReader(new InputStreamReader(
                conn.getInputStream(),
                "utf-8"));
        String line;
        List <String> ws = new ArrayList <>();
        while ((line = innet.readLine()) != null) {
            StringBuilder sb = new StringBuilder(line);
            for (int position = 0; position < line.length(); position++) {
                if (Character.isSpaceChar(line.charAt(position))) {
                    sb.replace(position, position + 1, separator);
                }
            }
            ws.add(sb.toString());
        }
        innet.close();
        return ws;
    }


    /**
     * For  given sentences,	return the spilt result
     *
     * @param sentences a string of words  a separator
     * @return an list of String show the words spilt by the separator
     */
    public static List <String> wordSegment(String sentences, String separator) {
        String pattern = "ws";

        try {
            String text = URLEncoder.encode(sentences, "utf-8");
            URL url = new URL("https://api.ltp-cloud.com/analysis/?"
                    + "api_key=" + api_key + "&"
                    + "text=" + text + "&"
                    + "format=" + format + "&"
                    + "pattern=" + pattern + "&"
                    + "only_ner=true");
            URLConnection conn = url.openConnection();
            conn.connect();

            BufferedReader innet = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(),
                    "utf-8"));
            String line;
            List <String> wordSegmentedTextList = new ArrayList <>();

            while ((line = innet.readLine()) != null) {
                StringBuilder sb = new StringBuilder(line);
                for (int position = 0; position < line.length(); position++) {
                    if (Character.isSpaceChar(line.charAt(position))) {
                        sb.replace(position, position + 1, separator);
                    }
                }
                wordSegmentedTextList.add(sb.toString());
            }
            innet.close();
            return wordSegmentedTextList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String wordSegmentAsString(String sentences, String separator) {
        String pattern = "ws";

        try {
            String text = URLEncoder.encode(sentences, "utf-8");
            URL url = new URL("https://api.ltp-cloud.com/analysis/?"
                    + "api_key=" + api_key + "&"
                    + "text=" + text + "&"
                    + "format=" + format + "&"
                    + "pattern=" + pattern + "&"
                    + "only_ner=true");
            URLConnection conn = url.openConnection();
            conn.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(),
                    "utf-8"));
            String line;
            // List <String> wordSegmentedTextList = new ArrayList <>();
            StringJoiner wordSegmentedText = new StringJoiner(separator);

            while ((line = bufferedReader.readLine()) != null) {
                StringBuilder oneSentenceOfText = new StringBuilder(line);
                for (int position = 0; position < line.length(); position++) {
                    if (Character.isSpaceChar(line.charAt(position))) {
                        oneSentenceOfText.replace(position, position + 1, separator);
                    }
                }
                // wordSegmentedTextList.add(sb.toString());
                wordSegmentedText.add(oneSentenceOfText.toString());
            }

            bufferedReader.close();
            return wordSegmentedText.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * For a given sentence,return the the	corresponding	POS	tag	sequence
     *
     * @param sentence an	array of words
     * @return an array	of WordPOSPair	objects	(defined	above)
     */
    public static WordPOSPair[] getPosTags(String[] sentence) throws IOException {
        String pattern = "pos";
        String text = sentence[0];
        for (int ord = 1; ord < sentence.length; ord++) {
            text += sentence[ord];

        }

        text = URLEncoder.encode(text, "utf-8");
        URL url = new URL("https://api.ltp-cloud.com/analysis/?"
                + "api_key=" + api_key + "&"
                + "text=" + text + "&"
                + "format=" + format + "&"
                + "pattern=" + pattern);
        URLConnection conn = url.openConnection();
        conn.connect();

        BufferedReader inner = new BufferedReader(new InputStreamReader(
                conn.getInputStream(),
                "utf-8"));
        String[] line = new String[sentence.length];

        int lineCount = -1;
        String readline;
        List <String> wordList = new ArrayList <>();
        List <String> posList = new ArrayList <>();

        int len = 0;
        while ((readline = inner.readLine()) != null) {
            // System.out.println(readline);
            String[] str = readline.split(" ");
            for (String s : str) {
                // System.out.println(s.indexOf("_"));
                wordList.add(s.substring(0, s.indexOf("_")));
                posList.add(s.substring(s.indexOf("_") + 1));
            }
            line[++lineCount] = readline;
            len += str.length;
        }

        WordPOSPair[] wordPOSPairs = new WordPOSPair[len];
        // initial();
        for (int i = 0; i < len; i++) {
            wordPOSPairs[i] = new WordPOSPair();
            wordPOSPairs[i].word = wordList.get(i);
            String str = posList.get(i);
            wordPOSPairs[i].pos = posMap.get(str);
        }
        inner.close();

        return wordPOSPairs;
    }


    public static WordPOSPair[] getPosTags(String sentences) {
        try {
            String pattern = "pos";
            String text = URLEncoder.encode(sentences, "utf-8");
            URL url = new URL("https://api.ltp-cloud.com/analysis/?"
                    + "api_key=" + api_key + "&"
                    + "text=" + text + "&"
                    + "format=" + format + "&"
                    + "pattern=" + pattern);
            URLConnection conn = url.openConnection();
            conn.connect();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(),
                    "utf-8"));

            String readline;
            List <String> wordList = new ArrayList <>();
            List <String> posList = new ArrayList <>();


            while ((readline = bufferedReader.readLine()) != null) {
                // System.out.println(readline);
                String[] str = readline.split(" ");
                for (String s : str) {
                    if (s.contains("_")) {
                        wordList.add(s.substring(0, s.indexOf("_")));
                        posList.add(s.substring(s.indexOf("_") + 1));
                    }
                }

            }

            WordPOSPair[] wordPOSPairs = new WordPOSPair[posList.size()];

            // initial();
            for (int i = 0; i < posList.size(); i++) {
                wordPOSPairs[i] = new WordPOSPair();
                wordPOSPairs[i].word = wordList.get(i);
                String str = posList.get(i);
                wordPOSPairs[i].pos = posMap.get(str);
            }
            bufferedReader.close();

            return wordPOSPairs;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * For a given sentence,	return the recognized named	entities
     *
     * @param sentence an	array of words
     * @return an array of NamedEnitity objects(defined above)
     */
    public static WordNamedEntityPair[] getNamedEntities(String[] sentence) throws IOException {
        String pattern = "ner";
        WordNamedEntityPair[] namedEntityTags = null;
        String text = sentence[0];
        for (int ord = 1; ord < sentence.length; ord++) {
            text += sentence[ord];
        }
        text = URLEncoder.encode(text, "utf-8");
        URL url = new URL("https://api.ltp-cloud.com/analysis/?"
                + "api_key=" + api_key + "&"
                + "text=" + text + "&"
                + "format=" + format + "&"
                + "pattern=" + pattern + "&"
                + "only_ner=true");
        URLConnection conn = url.openConnection();
        conn.connect();

        BufferedReader inner = new BufferedReader(new InputStreamReader(
                conn.getInputStream(),
                "utf-8"));
        String[] line = new String[sentence.length];
        List <String> wordList = new ArrayList <>();
        List <String> entityList = new ArrayList <>();
        String readline;
        while ((readline = inner.readLine()) != null) {
            wordList.add(readline.substring(0, readline.indexOf(" ")));
            entityList.add(readline.substring(readline.indexOf(" ") + 1));
        }
        namedEntityTags = new WordNamedEntityPair[wordList.size()];
        // initial();
        for (int i = 0; i < wordList.size(); i++) {
            namedEntityTags[i] = new WordNamedEntityPair();
            namedEntityTags[i].word = wordList.get(i);
            String str = entityList.get(i);
            namedEntityTags[i].namedEntity = nerMap.get(str);
        }
        inner.close();

        return namedEntityTags;
    }

    public static WordNamedEntityPair[] getNamedEntities(String sentences) {
        try {
            String pattern = "ner";
            WordNamedEntityPair[] namedEntityTags = null;
            String text = URLEncoder.encode(sentences, "utf-8");
            URL url = new URL("https://api.ltp-cloud.com/analysis/?"
                    + "api_key=" + api_key + "&"
                    + "text=" + text + "&"
                    + "format=" + format + "&"
                    + "pattern=" + pattern + "&"
                    + "only_ner=true");
            URLConnection conn = url.openConnection();
            conn.connect();

            BufferedReader inner = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(),
                    "utf-8"));

            List <String> wordList = new ArrayList <>();
            List <String> entityList = new ArrayList <>();
            String readline;
            while ((readline = inner.readLine()) != null) {
                wordList.add(readline.substring(0, readline.indexOf(" ")));
                entityList.add(readline.substring(readline.indexOf(" ") + 1));
            }
            namedEntityTags = new WordNamedEntityPair[wordList.size()];
            // initial();
            for (int i = 0; i < wordList.size(); i++) {
                namedEntityTags[i] = new WordNamedEntityPair();
                namedEntityTags[i].word = wordList.get(i);
                String str = entityList.get(i);
                namedEntityTags[i].namedEntity = nerMap.get(str);
            }
            inner.close();

            return namedEntityTags;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * For a given sentence,	return the Semantic Role Labeling
     *
     * @param sentence an	array of words
     * @return an list of String show the Semantic Role Labeling
     */
    public static List <String> getSrl(String[] sentence) throws IOException {
        String pattern = "srl";
        String text = sentence[0];
        for (int ord = 1; ord < sentence.length; ord++) {
            text += sentence[ord];
        }
        text = URLEncoder.encode(text, "utf-8");
        URL url = new URL("https://api.ltp-cloud.com/analysis/?"
                + "api_key=" + api_key + "&"
                + "text=" + text + "&"
                + "format=" + format + "&"
                + "pattern=" + pattern + "&"
                + "only_ner=true");
        URLConnection conn = url.openConnection();
        conn.connect();

        BufferedReader innet = new BufferedReader(new InputStreamReader(
                conn.getInputStream(),
                "utf-8"));
        String line;
        List <String> srl = new ArrayList <>();
        while ((line = innet.readLine()) != null) {
            srl.add(line);
        }


        innet.close();
        return srl;
    }

    public static void main(String[] args) throws IOException {
        String[] sentence = new String[3];
        sentence[0] = "中国的北京大学即将进入可怕的期末季。国务院总理李克强昨天在中南海把美国部长安排的明明白白。";
        sentence[1] = "国务院总理李克强昨天在中南海把美国部长安排的明明白白。";
        sentence[2] = "我昨天在图书馆砍了一本叫《骑马与砍杀》的书。";

        List <String> results = NLPServices.wordSegment(sentence, "`");

        for (String item : results) {
            System.out.println(item);
        }
    }

}