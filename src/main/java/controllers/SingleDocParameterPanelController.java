package controllers;

import com.jfoenix.effects.JFXDepthManager;
import javafx.fxml.FXML;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class SingleDocParameterPanelController {

    @FXML
    private VBox analyzeVBox, inputFileSettingsVBox, outputFileSettingsVBox;

    public void initialize() {
        this.setComponentDepths();
    }

    private void setComponentDepths() {
        analyzeVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        JFXDepthManager.setDepth(analyzeVBox, 2);

        inputFileSettingsVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        JFXDepthManager.setDepth(inputFileSettingsVBox, 2);

        outputFileSettingsVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(2), null)));
        JFXDepthManager.setDepth(outputFileSettingsVBox, 2);
    }
}
