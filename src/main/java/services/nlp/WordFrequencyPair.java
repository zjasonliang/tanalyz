package services.nlp;

public class WordFrequencyPair {
    String word;
    Integer frequency;

    public WordFrequencyPair(String word, Integer frequency) {
        this.word = word;
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return String.format("(%s, %d)", word, frequency);
    }
}
