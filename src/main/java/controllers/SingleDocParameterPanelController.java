package controllers;

import com.jfoenix.effects.JFXDepthManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.DataModel;
import services.highlight.HTMLHighlighter;
import services.nlp.NER;
import services.nlp.POS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleDocParameterPanelController {

    @FXML
    private VBox analyzeVBox, inputFileSettingsVBox, outputFileSettingsVBox;

    @FXML
    private HBox controlHBox;

    @FXML
    private Button analyzeButton, stopButton;

    @FXML
    private CheckBox nounCheckBox, verbCheckBox, adjectiveCheckBox, adverbCheckBox,
            locationCheckBox, personCheckBox, organizationCheckBox,
            wordFreqCheckBox, topicCheckBox;

    @FXML private ChoiceBox<String> chooseFromChoiceBox;

    DataModel model;

    public void initialize() {
        setComponentDepths();
        initChooseFromChoiceBox();
    }

    private void initChooseFromChoiceBox() {
        chooseFromChoiceBox.setStyle("-fx-font-size:11");
        chooseFromChoiceBox.getItems().addAll("Current Page", "File");
        chooseFromChoiceBox.setValue("Current Page");
    }

    public void initDataModel(DataModel model) {
        this.model = model;
    }

    private void setComponentDepths() {
        // controlHBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        // JFXDepthManager.setDepth(controlHBox, 2);

        analyzeVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        JFXDepthManager.setDepth(analyzeVBox, 2);

        inputFileSettingsVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        JFXDepthManager.setDepth(inputFileSettingsVBox, 2);

        outputFileSettingsVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        JFXDepthManager.setDepth(outputFileSettingsVBox, 2);
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

            // root = FXMLLoader.load(getClass().getClassLoader().getResource("views/ResultsWindow.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // root.getStylesheets().add(getClass().getClassLoader().getResource("css/materialfx-v0_3.css").toString());
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void handleAnalyzeButtonClicked() {
        List<POS> posList = new ArrayList <POS>() {{
            add(POS.ADVERB);
            add(POS.ADJECTIVE);
            add(POS.TEMPORAL_NOUN);
        }};

        List<NER> nerList = new ArrayList <NER>() {{
           add(NER.INSTITUTION_NAME);
           add(NER.PERSON_NAME);
           add(NER.PLACE_NAME);
        }};

        String highlightedHTML = HTMLHighlighter.highlightWordsInExtractedHTML(model.currentlyLoadedText, posList, nerList);

        model.webEngine.loadContent(highlightedHTML);

        showResultsWindow();
    }
}