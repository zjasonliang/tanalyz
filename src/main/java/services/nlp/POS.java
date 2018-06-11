package services.nlp;


class PoSColor {
    static String NOUN_STYLE = "style=\"color: #0071bc \"";
    static String VERB_STYLE = "style=\"color: #cd2026 \"";
    static String ADJECTIVE_STYLE = "style=\"color: #fdb81e \"";
    static String ADVERB_STYLE = "style=\"color: #2e8540 \"";

    static String DEFAULT_STYLE = "style=\"\"";

    // static String NOUN_STYLE = "style=\"bacground-color: white; color: #0071bc \"";
    // static String VERB_STYLE = "style=\"bacground-color: white; color: #cd2026 \"";
    // static String ADJECTIVE_STYLE = "style=\"bacground-color: white; color: #fdb81e \"";
    // static String ADVERB_STYLE = "style=\"bacground-color: white; color: #2e8540 \"";
    //
    // static String DEFAULT_STYLE = "style=\"bacground-color:white;color:black \"";
}

public enum POS {
    ADJECTIVE (PoSColor.ADJECTIVE_STYLE),
    OTHER_NOUN_MODIFIER,
    CONJUNCTION,
    ADVERB (PoSColor.ADVERB_STYLE),
    EXCLAMATION,
    MORPHEME,
    PREFIX,
    IDIOM,
    ABBREVIATION,
    SUFFIX,
    NUMBER,
    GENERAL_NOUN (PoSColor.NOUN_STYLE),
    DIRECTION_NOUN (PoSColor.NOUN_STYLE),
    PERSON_NAME,
    ORGANIZATION_NAME,
    LOCATION_NOUN (PoSColor.NOUN_STYLE),
    GEOGRAPHICAL_NAME,
    TEMPORAL_NOUN (PoSColor.NOUN_STYLE),
    OTHER_PROPER_NOUN (PoSColor.NOUN_STYLE),
    ONOMATOPOEIA,
    PREPOSITION,
    QUANTITY,
    PRONOUN,
    AUXILIARY,
    VERB (PoSColor.VERB_STYLE),
    PUNCTUATION,
    FOREIGN_WORDS,
    NON_LEXEME;

    public final String colorStyle;

    POS() {
        this.colorStyle = PoSColor.DEFAULT_STYLE;
    }

    POS(String colorStyle) {
        this.colorStyle = colorStyle;
    }
}
