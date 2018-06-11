package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import model.DataModel;
import services.nlp.NLPServices;
import services.nlp.WordFrequencyPair;

import java.util.List;

public class ResultsWindowController {

    private DataModel model;

    @FXML private BarChart<String, Integer> wordFrequencyBarChart;

    public void initialize() {
        wordFrequencyBarChart.setTitle("Word Frequency Analysis");

        List<WordFrequencyPair> list = model.currentlyLoadedText.getWordFrequencySortedList();

        XYChart.Series series = new XYChart.Series();

        // System.out.println(NLPServices.stopWordSet);

        int count = 0;
        for (WordFrequencyPair pair : list) {
            if (!NLPServices.stopWordSet.contains(pair.word)) {
                series.getData().add(new XYChart.Data(pair.word, pair.frequency));
                // System.out.println("==>" + pair.word + "<==");
                count++;
            }
            if (count == 20) break;
        }
        // for (int i = 0; i < 10; i++) {
        //     WordFrequencyPair item = list.get(i);
        //     series.getData().add(new XYChart.Data(item.word, item.frequency));
        // }

        wordFrequencyBarChart.getData().add(series);
        wordFrequencyBarChart.setLegendVisible(false);
    }

    void initDataModel(DataModel model) {
        this.model = model;
    }
}
