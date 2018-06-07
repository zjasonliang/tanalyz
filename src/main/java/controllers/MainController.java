package controllers;

import com.jfoenix.effects.JFXDepthManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.Objects;


public class MainController {

    @FXML
    private WebView webView;

    @FXML
    private AnchorPane webViewAnchorPane, rightAnchorPane;

    @FXML
    private TextField urlTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Button tagButton, posButton, nerButton, findButton, settingsButton, moreButton;

    private WebEngine webEngine;

    public void initialize() {
        this.setComponentDepths();
        this.webEngine = this.webView.getEngine();
        this.webEngine.load("https://www.baidu.com");
        this.enableTextFieldAutoCompletion();
    }

    private void setComponentDepths() {
        JFXDepthManager.setDepth(tagButton, 2);
        JFXDepthManager.setDepth(posButton, 2);
        JFXDepthManager.setDepth(nerButton, 2);
        JFXDepthManager.setDepth(findButton, 2);
        JFXDepthManager.setDepth(settingsButton, 2);
        JFXDepthManager.setDepth(moreButton, 2);

        JFXDepthManager.setDepth(searchButton, 2);

        JFXDepthManager.setDepth(webViewAnchorPane, 2);
    }

    private void enableTextFieldAutoCompletion() {
        String [] suggestions = {"http://baidu.com", "http://pku.edu.cn", "http://sina.com"};
        AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(urlTextField, suggestions);

        binding.setMinWidth(300);
    }

    private void handleSearchRequest(ActionEvent event) {
        String url = urlTextField.getText();

        if (!url.toLowerCase().matches("^\\w+://.*")) {
            url = "http://" + url;
        }

        this.webEngine.load(url);
    }

    @FXML
    public void onEnter(ActionEvent event) {
        if (Objects.equals("urlTextField", ((Control) event.getSource()).getId()))
            handleSearchRequest(event);
    }

    public void showEditor(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("views/Editor.fxml"));
        root.getStylesheets().add(getClass().getResource("css/materialfx-v0_3.css").toString());
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void handleTagButtonClicked(ActionEvent event) {
        PopOver popOver = new PopOver();

        VBox vBox = new VBox();

        popOver.setContentNode(vBox);

        popOver.show(tagButton);  // The target is the button.
        // popOver.getRoot().getStylesheets().add()
    }

    private void showNoPageLoadedAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Page Loaded");
        alert.setHeaderText(null);
        alert.setContentText("There is no page class loaded. Please try typing in a valid URL or open an HTML file.");
        alert.showAndWait();
    }

    public void handleSingleDocButtonClicked(ActionEvent event) {
        if (this.webEngine.getDocument() == null) {
            showNoPageLoadedAlert();
        } else {
            rightAnchorPane.getChildren().clear();
            try {
                rightAnchorPane.getChildren().add(FXMLLoader
                        .load(getClass().getClassLoader().getResource("views/SingleDocParameterPanel.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handlePOSButtonClicked(ActionEvent event) throws IOException {
        if (this.webEngine.getDocument() == null) {
            showNoPageLoadedAlert();
        } else {
            rightAnchorPane.getChildren().clear();
            rightAnchorPane.getChildren().add(FXMLLoader
                    .load(getClass().getClassLoader().getResource("views/POSParameterPanel.fxml")));
        }
    }

    public void handleNERButtonClicked(ActionEvent event) throws IOException {
        if (this.webEngine.getDocument() == null) {
            showNoPageLoadedAlert();
        } else {
            rightAnchorPane.getChildren().clear();
            rightAnchorPane.getChildren().add(FXMLLoader
                    .load(getClass().getClassLoader().getResource("views/NERParameterPanel.fxml")));
        }
    }

}
