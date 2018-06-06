package services.spellcheck;

/**
 * This class combines the candidate word and its score.
 * @author Chase Hwong
 *
 */
public class SpellingCandidate {
	String word;
	float score;
	public SpellingCandidate(String word, float score) {
		this.word = word;
		this.score = score;
	}
}
