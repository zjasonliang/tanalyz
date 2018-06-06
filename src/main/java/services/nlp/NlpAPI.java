package services.nlp;

/**
 * Use java to build a http connection and requesst the ltp-cloud service for some simple
 * Natural Language Processing analysis,the results are in plain formats
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NlpAPI{
    public static String api_key = "L188H9h095NEaKVUnnQz2XnBlzQVqTLNckqVRn7I";
    public static String format="plain";
    public static String pattern;
    public static String text=null;
    public static int len=0;


    /**
     * For a given sentence,return the the	corresponding	POS	tag	sequence
     * @param sentence   an	array of words
     * @return an array	of NamedPos	objects	(defined	above)
     */
    public  NamedPos[] getPosTags(String[] sentence) throws IOException {
        pattern = "pos";
        NamedPos[] namedPosTags = null;
        text = sentence[0];
        for(int ord = 1 ; ord < sentence.length ; ord++) {
            text += sentence[ord];

        }


        text = URLEncoder.encode(text,"utf-8");
        URL url = new URL("https://api.ltp-cloud.com/analysis/?"
                + "api_key=" + api_key + "&"
                + "text="    + text    + "&"
                + "format="  + format  + "&"
                + "pattern=" + pattern);
        URLConnection conn = url.openConnection();
        conn.connect();

        BufferedReader inner = new BufferedReader(new InputStreamReader(
                conn.getInputStream(),
                "utf-8"));
        String []line=new String[sentence.length];

        int linecount = -1;
        String readline;
        List<String> wordList = new ArrayList<>();
        List<String> posList = new ArrayList<>();
        while ((readline = inner.readLine())!= null) {
            String[] str = readline.split(" ");
            for(String s : str) {
                wordList.add(s.substring(0,s.indexOf("_")));
                posList.add(s.substring(s.indexOf("_")+1));
            }
            line[++linecount] = readline;
            len+= str.length;
        }

        namedPosTags = new NamedPos[len];
        for(int i=0;i<len;i++) {
            namedPosTags[i]=new NamedPos();
            namedPosTags[i].word = wordList.get(i);
            namedPosTags[i].namedPos = posList.get(i);
        }
        inner.close();
        return namedPosTags;


    }


    /**
     * For a given sentence,	return the recognized named	entities
     * @param sentence   an	array of words
     * @return an array of NamedEnitity objects(defined above)
     *
     */
    public NamedEntity[] getNamedEntity(String[] sentence)throws IOException{
        pattern = "ner";
        NamedEntity[] namedEntityTags=null;
        text = sentence[0];
        for(int ord = 1 ; ord < sentence.length ; ord++) {
            text += sentence[ord];
        }
        text = URLEncoder.encode(text,"utf-8");
        URL url     = new URL("https://api.ltp-cloud.com/analysis/?"
                + "api_key=" + api_key + "&"
                + "text="    + text    + "&"
                + "format="  + format  + "&"
                + "pattern=" + pattern+"&"
                +"only_ner=true");
        URLConnection conn = url.openConnection();
        conn.connect();

        BufferedReader inner = new BufferedReader(new InputStreamReader(
                conn.getInputStream(),
                "utf-8"));
        String[] line=new String[sentence.length];
        List<String> wordList = new ArrayList<>();
        List<String> entityList = new ArrayList<>();
        String readline;
        while ((readline = inner.readLine())!= null) {
            wordList.add(readline.substring(0, readline.indexOf(" ")));
            entityList.add(readline.substring(readline.indexOf(" ")+1));
        }
        namedEntityTags = new NamedEntity[wordList.size()];
        for(int i = 0;i < wordList.size(); i++ ) {
            namedEntityTags[i] = new NamedEntity();
            namedEntityTags[i].word = wordList.get(i);
            namedEntityTags[i].namedEntityType = entityList.get(i);
        }
        inner.close();
        return namedEntityTags;

    }

    /* Test
    public static void main(String[] args) throws IOException{
        String[] sentence=new String[3];
        sentence[0]="中国的北京大学即将进入可怕的期末季。";
        sentence[1]="国务院总理李克强昨天在中南海把美国部长安排的明明白白。";
        sentence[2]="我昨天在图书馆砍了一本叫《骑马与砍杀》的书。";
        NlpAPI demo=new NlpAPI();

        NamedEntity[] pos=demo.getNamedEntity(sentence);
        for(int j=0;j<pos.length;j++)
            System.out.println(j+pos[j].word+"――"+pos[j].namedEntityType);
    }
    */
}
