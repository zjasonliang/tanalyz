package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

import javax.swing.*;
import java.io.IOException;
import java.util.Objects;


public class MainController {
    @FXML
    private WebView webView;

    @FXML
    private TextField urlTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Button tagButton;

    private WebEngine webEngine;

    public void initialize() {
        this.webEngine = this.webView.getEngine();
        this.webEngine.load("https://www.baidu.com");
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
}
