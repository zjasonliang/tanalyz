package controllers;

import com.jfoenix.effects.JFXDepthManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

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

    public void initialize() {
        this.setComponentDepths();
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
            root = FXMLLoader.load(getClass().getClassLoader().getResource("views/ResultsWindow.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        root.getStylesheets().add(getClass().getClassLoader().getResource("css/materialfx-v0_3.css").toString());
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void analyze() {
        showResultsWindow();

    }
}