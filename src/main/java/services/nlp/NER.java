package services.nlp;

class NerColor {
    static final String INSTITUTION_NAME_STYLE = "style=\"background-color: #494440; color: white; \"";
    static final String PERSON_NAME_STYLE = "style=\"background-color: #5b616b; color: white; \"";
    static final String PLACE_NAME_STYPLE = "style=\"background-color: #d45440; color: white; \"";

    static final String DEFAULT_STYLE = "style=\"background-color: white; color: black; \"";
}

public enum NER {
    INSTITUTION_NAME (NerColor.INSTITUTION_NAME_STYLE),
    PERSON_NAME (NerColor.PERSON_NAME_STYLE),
    PLACE_NAME (NerColor.PLACE_NAME_STYPLE);

    public final String colorStyle;

    NER() {
        this.colorStyle = NerColor.DEFAULT_STYLE;
    }

    NER(String colorStyle) {
        this.colorStyle = colorStyle;
    }
}
