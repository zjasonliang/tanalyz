package controllers;

import com.jfoenix.effects.JFXDepthManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.DataModel;
import model.Prechecked;
import services.highlight.HTMLHighlighter;
import services.nlp.NER;
import services.nlp.POS;
import services.util.imgutil.ImageViewer;
import services.wordcloud.CircularWordCloud;
import sun.misc.resources.Messages_de;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for parameter panel (right panel in the main window)
 */
public class SingleDocParameterPanelController {

    @FXML
    private VBox analyzeButtonVBox, analyzeVBox;

    @FXML
    private ProgressBar analysisProgressBar;

    @FXML
    private HBox controlHBox;

    @FXML
    private Button analyzeButton, stopButton;

    @FXML
    private CheckBox nounCheckBox, verbCheckBox, adjectiveCheckBox, adverbCheckBox,
            locationCheckBox, personCheckBox, organizationCheckBox,
            wordFreqCheckBox, wordCloudCheckBox;

    @FXML
    private ChoiceBox <String> chooseFromChoiceBox;

    Task <Void> analysisTask;

    DataModel model;

    CircularWordCloud wordCloud;

    public void initialize() {
        setComponentDepths();
        initAnalyzeProgressBar();
        initPrechecked();

        analyzeButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white");
        stopButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white");
        stopButton.setDisable(true);
    }

    private void initPrechecked() {

        switch (model.prechecked) {
            case POS:
                nounCheckBox.setSelected(true);
                verbCheckBox.setSelected(true);
                adverbCheckBox.setSelected(true);
                adjectiveCheckBox.setSelected(true);
                break;

            case NER:
                locationCheckBox.setSelected(true);
                personCheckBox.setSelected(true);
                organizationCheckBox.setSelected(true);
                break;

            case WORD_FREQ:
                wordFreqCheckBox.setSelected(true);
                break;

            case WORD_CLOUD:
                wordCloudCheckBox.setSelected(true);
                break;
        }
    }

    private void initAnalyzeProgressBar() {
        // analysisProgressBar.setStyle("-fx-padding: 0.05em;");
        analysisProgressBar.setVisible(false);
        analysisProgressBar.setStyle("-fx-background-color: linear-gradient(\n" +
                "        from 0px .75em to .75em 0px,\n" +
                "        repeat,\n" +
                "        -fx-accent 0%,\n" +
                "        -fx-accent 49%,\n" +
                "        derive(-fx-accent, 30%) 50%,\n" +
                "        derive(-fx-accent, 30%) 99%\n" +
                "    );");
    }

    public void initDataModel(DataModel model) {
        this.model = model;
    }

    private void setComponentDepths() {
        // controlHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        // JFXDepthManager.setDepth(controlHBox, 2);

        analyzeButtonVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        JFXDepthManager.setDepth(analyzeButtonVBox, 2);

        analyzeVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        JFXDepthManager.setDepth(analyzeVBox, 2);
    }

    private void showResultsWindow() {
        Stage stage = new Stage();
        Parent root = null;

        try {
            ResultsWindowController resultsWindowController = new ResultsWindowController();
            resultsWindowController.initDataModel(model);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/ResultsWindow.fxml"));
            loader.setController(resultsWindowController);

            root = loader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showWordCloudWindow() {
        Stage stage = new Stage();
        Parent root = null;

        try {
            ImageViewerController imageViewerController = new ImageViewerController();
            imageViewerController.initDataModel(model);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/ImageViewer.fxml"));
            loader.setController(imageViewerController);

            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setScene(new Scene(root));
        stage.show();
    }

    public void onStopButtonClicked() {
        if (analysisTask == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Analysis Running");
            alert.setHeaderText(null);
            alert.setContentText("There is no running analysis class loaded.");
            alert.showAndWait();
        } else {
            analysisTask.cancel();
            analysisProgressBar.setProgress(0);
            analysisProgressBar.setVisible(false);
            analyzeButton.setDisable(false);
            stopButton.setDisable(true);
        }
    }

    private void progressBarTo(ProgressBar progressBar, double toValue) {
        Timeline timeline = new Timeline();

        KeyValue keyValue = new KeyValue(progressBar.progressProperty(), toValue);
        KeyFrame keyFrame = new KeyFrame(new Duration(1000), keyValue);

        timeline.getKeyFrames().add(keyFrame);

        timeline.play();

    }

    public void handleAnalyzeButtonClicked() {

        if (model.currentlyLoadedText.getTextLength() > 2000) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Text too Long Error");
            alert.setHeaderText(null);
            alert.setContentText("Length of text in page exceeds 2000 chinese characters. Please try a shorter one.");
            alert.showAndWait();
            return;
        }

        analyzeButton.setDisable(true);
        stopButton.setDisable(false);

        analysisTask = new Task <Void>() {
            @Override
            protected Void call() throws Exception {
                analysisProgressBar.setVisible(true);
                analysisProgressBar.setProgress(0);

                List <POS> posList = new ArrayList <POS>();
                List <NER> nerList = new ArrayList <NER>();

                if (nounCheckBox.isSelected()) {
                    posList.add(POS.GENERAL_NOUN);
                    posList.add(POS.DIRECTION_NOUN);
                    posList.add(POS.LOCATION_NOUN);
                    posList.add(POS.TEMPORAL_NOUN);
                    posList.add(POS.OTHER_PROPER_NOUN);
                }

                if (verbCheckBox.isSelected()) {
                    posList.add(POS.VERB);
                }

                if (adverbCheckBox.isSelected()) {
                    posList.add(POS.ADJECTIVE);
                }

                if (adjectiveCheckBox.isSelected()) {
                    posList.add(POS.ADJECTIVE);
                }

                if (locationCheckBox.isSelected()) {
                    nerList.add(NER.PLACE_NAME);
                }

                if (personCheckBox.isSelected()) {
                    nerList.add(NER.PERSON_NAME);
                }

                if (organizationCheckBox.isSelected()) {
                    nerList.add(NER.INSTITUTION_NAME);
                }

                progressBarTo(analysisProgressBar, 0.1);

                if (wordCloudCheckBox.isSelected()) {
                    String text = model.currentlyLoadedText.getWordSegmentedExtractedPlainText();
                    System.out.println(text);
                    wordCloud = new CircularWordCloud(text);
                    model.wordCloud = new Image(wordCloud.getSavedImageRelativePath());
                }

                progressBarTo(analysisProgressBar, 0.4);

                model.currentlyLoadedText.highlightedExtractedHTML = HTMLHighlighter.highlightWordsInExtractedHTML(model.currentlyLoadedText, posList, nerList);

                // analysisProgressBar.setProgress(0.6);
                progressBarTo(analysisProgressBar, 1);

                analyzeButton.setDisable(false);
                stopButton.setDisable(true);


                Platform.runLater(() -> {
                    model.webEngine.loadContent(model.currentlyLoadedText.highlightedExtractedHTML);

                    if (wordFreqCheckBox.isSelected()) {
                        showResultsWindow();
                    }

                    if (wordCloudCheckBox.isSelected()) {
                        showWordCloudWindow();
                    }
                });

                analysisTask = null;
                return null;
            }
        };
        new Thread(analysisTask).start();
    }
}