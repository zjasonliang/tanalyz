package services.wordcloud;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CircularWordCloud {

    private String savedImageRelativePath;

    public CircularWordCloud(String text) {

        final FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(new ArrayList <String>(){{add(text);}});
        final Dimension dimension = new Dimension(600, 600);

        final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(300));
        wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
        wordCloud.setFontScalar(new SqrtFontScalar(10, 100));
        wordCloud.build(wordFrequencies);

        String path = getClass().getClassLoader().getResource("").getPath();

        File file = new File(path + "wordclouds");
        file.mkdirs();

        this.savedImageRelativePath = "wordclouds/circular-" + new Date().getTime() + ".png";
        wordCloud.writeToFile(path + this.savedImageRelativePath);
    }

    public String getSavedImageRelativePath() {
        return savedImageRelativePath;
    }

    public static void main(String[] args) {
        System.out.println(new Date().getTime());
    }

}
