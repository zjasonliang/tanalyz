package services.spellcheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

/**
 * This class provides the functionality of checking if a word is in the dictionary, and correcting the misspelled words.
 *
 * @author Chase Hwong
 */
public class SpellingCorrector {
    /**
     * This method can check if a word is in the dictionary.
     *
     * @param word to be checked
     * @return true if the word is in the dictionary
     */
    public static boolean check(String word) {
        return dic.containsKey(word.toLowerCase());
    }

    /**
     * This method can correct a misspelled word by giving the candidate words that might be correctly spelled.
     * <br>Every candidate will be scored. Higher score means higher possibility. The full score is 100.0.
     *
     * @param previousWord to be corrected
     * @return an array of the candidate words as the score from high to low
     * <br>Specially, if the word is in the dictionary, the method returns itself with a score 100.0
     */
    public static SpellingCandidate[] correct(String previousWord) {
        String word = previousWord.toLowerCase();
        if (dic.containsKey(word)) {
            SpellingCandidate[] candidates = {new SpellingCandidate(previousWord, (float) 100.0)};
            return candidates;
        }
        int wordCase;
        if (previousWord.compareTo(word) == 0)
            wordCase = LOWERCASE;
        else if (previousWord.compareTo(previousWord.toUpperCase()) == 0)
            wordCase = UPPERCASE;
        else
            wordCase = FIRSTCASE;
        int distance = 1;
        final int maxCount = 8;
        final int maxDistance = 2;
        HashSet <String> editSet = new HashSet <>();
        TreeMap <Float, String> map = new TreeMap <>();
        while (editSet.size() < maxCount && distance <= maxDistance) {
            editSet.addAll(edit(distance, word));
            Iterator <String> it = editSet.iterator();
            while (it.hasNext()) {
                String s = it.next();
                if (!map.containsValue(s)) {
                    map.put((float) (dic.get(s) / Math.pow(2, distance - 1)), s);
                }
            }
            distance++;
        }
        final float avgF = (float) 34.65;//the average frequency of a word
        final float minF = (float) 2.8;//1 / (1 + avgF)
        if (map.isEmpty()) {
            SpellingCandidate[] candidates = {new SpellingCandidate(previousWord, minF)};
            return candidates;
        }
        ArrayList <SpellingCandidate> list = new ArrayList <>();
        final int maxReturn = 5;
        while (list.size() < maxReturn && !map.isEmpty()) {
            Entry <Float, String> maxEntry = map.lastEntry();
            float key = maxEntry.getKey() / (maxEntry.getKey() + avgF) * 100;
            String value = CaseChage(maxEntry.getValue(), wordCase);
            list.add(new SpellingCandidate(value, Float.parseFloat(String.format("%.1f", key))));
            map.remove(maxEntry.getKey());
        }
        SpellingCandidate[] candidates = new SpellingCandidate[list.size()];
        list.toArray(candidates);
        return candidates;
    }

    private static final int LOWERCASE = 0;
    private static final int UPPERCASE = 1;
    private static final int FIRSTCASE = 2;
    private static Hashtable <String, Integer> dic = getDic();
    private static final char[] alphabet = {97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122};

    private static String CaseChage(String word, int wordCase) {
        switch (wordCase) {
            case LOWERCASE:
                return word;
            case UPPERCASE:
                return word.toUpperCase();
            case FIRSTCASE:
                char[] wc = word.toCharArray();
                wc[0] -= 32;
                return String.valueOf(wc);
        }
        return null;
    }

    public static Hashtable <String, Integer> getDic() {
        Hashtable <String, Integer> dic = new Hashtable <>();

        ClassLoader classLoader = SpellingCorrector.class.getClassLoader();
        File inFile = new File(classLoader.getResource("spellcheck/dictionary.txt").getFile());

        try {
            Scanner fin = new Scanner(new FileInputStream(inFile));
            while (fin.hasNext()) {
                String words = fin.next();
                int frequency = fin.nextInt();
                dic.put(words, frequency);
            }
            fin.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dic;
    }

    private static HashSet <String> edit(int distance, String word) {
        HashSet <String> wordSet = new HashSet <>();
        int length = word.length();
        for (int i = 0; i < length; i++) {
            StringBuilder editWord = new StringBuilder(word);
            String newWord = editWord.deleteCharAt(i).toString();
            if (distance > 1)
                wordSet.addAll(edit(distance - 1, newWord));
            else if (dic.containsKey(newWord))
                wordSet.add(newWord);
        }
        for (int i = 0; i < length - 1; i++) {
            StringBuilder editWord = new StringBuilder(word);
            String newWord = editWord.insert(i + 2, editWord.charAt(i)).deleteCharAt(i).toString();
            if (distance > 1)
                wordSet.addAll(edit(distance - 1, newWord));
            else if (dic.containsKey(newWord))
                wordSet.add(newWord);
        }
        for (int i = 0; i < length; i++) {
            char previous = word.charAt(i);
            for (int j = 0; j < 26 && alphabet[j] != previous; j++) {
                StringBuilder editWord = new StringBuilder(word);
                String newWord = editWord.deleteCharAt(i).insert(i, alphabet[j]).toString();
                if (distance > 1)
                    wordSet.addAll(edit(distance - 1, newWord));
                else if (dic.containsKey(newWord))
                    wordSet.add(newWord);
            }
        }
        for (int i = 0; i <= length; i++) {
            StringBuilder editWord = new StringBuilder(word);
            for (char e : alphabet) {
                if (e > 'a')
                    editWord.deleteCharAt(i);
                String newWord = editWord.insert(i, e).toString();
                if (distance > 1)
                    wordSet.addAll(edit(distance - 1, newWord));
                else if (dic.containsKey(newWord))
                    wordSet.add(newWord);
            }
        }
        return wordSet;
    }

    public static void main(String[] args) {
        getDic();
    }

}
