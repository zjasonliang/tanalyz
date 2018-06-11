package controllers;

import com.jfoenix.effects.JFXDepthManager;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.DataModel;
import model.Prechecked;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.w3c.dom.html.HTMLDocument;
import services.nlp.Text;
import services.util.HTMLHelper;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;


public class MainController {

    @FXML
    private WebView webView;

    @FXML
    private AnchorPane webViewAnchorPane, rightAnchorPane;

    @FXML
    private TextField urlTextField;

    @FXML
    private Button searchButton, extractButton;

    @FXML
    private Button singleDocButton,
            posButton, nerButton, wordFrequencyButton, wordCloudButton,
            settingsButton, moreButton;

    private WebEngine webEngine;

    private File openedHTMLFile;

    private DataModel model;

    public void initialize() {
        this.setComponentDepths();
        this.webEngine = this.webView.getEngine();
        model.webEngine = this.webEngine;
        // this.webEngine.load("https://en.wikipedia.org/wiki/Neuroscience");
        loadPage("http://pku.edu.cn/about/index.htm");
        this.enableTextFieldAutoCompletion();
    }

    public void initDataModel(DataModel model) {
        this.model = model;
    }

    public void loadPage(String url) {
        this.webEngine.load(url);

        this.webEngine.getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends Worker.State> observable,
                 Worker.State oldValue,
                 Worker.State newValue) -> {
                    if( newValue != Worker.State.SUCCEEDED ) {
                        return;
                    }
                    String htmlString = HTMLHelper.getDocumentString(this.webEngine.getDocument());
                    model.currentlyLoadedText = new Text(htmlString);
                } );
    }

    private void setComponentDepths() {
        JFXDepthManager.setDepth(singleDocButton, 2);
        // JFXDepthManager.setDepth(multipleDocsButton, 2);
        // JFXDepthManager.setDepth(tagButton, 2);
        JFXDepthManager.setDepth(posButton, 2);
        JFXDepthManager.setDepth(nerButton, 2);
        // JFXDepthManager.setDepth(findButton, 2);
        JFXDepthManager.setDepth(wordFrequencyButton, 2);
        JFXDepthManager.setDepth(wordCloudButton, 2);
        JFXDepthManager.setDepth(settingsButton, 2);
        JFXDepthManager.setDepth(moreButton, 2);

        JFXDepthManager.setDepth(searchButton, 2);
        JFXDepthManager.setDepth(extractButton, 2);

        JFXDepthManager.setDepth(webViewAnchorPane, 2);
    }

    private void enableTextFieldAutoCompletion() {
        String [] suggestions = {
                "http://baidu.com",
                "http://pku.edu.cn",
                "http://sina.com",
                "http://pku.edu.cn/about/index.htm"
        };

        AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(urlTextField, suggestions);

        binding.setMinWidth(300);
    }

    private void handleSearchRequest(ActionEvent event) {
        String url = urlTextField.getText();

        if (!url.toLowerCase().matches("^\\w+://.*")) {
            url = "http://" + url;
        }

        // this.webEngine.load(url);
        loadPage(url);
    }

    public void onSearchButtonClicked(ActionEvent event) {
        handleSearchRequest(event);
    }

    public void onEnter(ActionEvent event) {
        if (Objects.equals("urlTextField", ((Control) event.getSource()).getId()))
            handleSearchRequest(event);
    }

    public void showEditor(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/Editor.fxml"));
        root.getStylesheets().add(getClass().getClassLoader().getResource("css/materialfx-v0_3.css").toString());
        stage.setScene(new Scene(root));
        stage.show();
    }

    // public void handleTagButtonClicked(ActionEvent event) {
    //     PopOver popOver = new PopOver();
    //
    //     VBox vBox = new VBox();
    //
    //     popOver.setContentNode(vBox);
    //
    //     popOver.show(tagButton);  // The target is the button.
    //     // popOver.getRoot().getStylesheets().add()
    // }

    private void showNoPageLoadedAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Page Loaded");
        alert.setHeaderText(null);
        alert.setContentText("There is no page class loaded. Please try typing in a valid URL or open an HTML file.");
        alert.showAndWait();
    }

    private void showRightPanel() {
        if (HTMLHelper.isDocumentNull(this.webEngine.getDocument())) {
            showNoPageLoadedAlert();
        } else {
            rightAnchorPane.getChildren().clear();
            try {
                SingleDocParameterPanelController
                        singleDocParameterPanelController =
                        new SingleDocParameterPanelController();
                singleDocParameterPanelController.initDataModel(model);

                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/SingleDocParameterPanel.fxml"));
                loader.setController(singleDocParameterPanelController);

                rightAnchorPane.getChildren().add(loader.load());

                // rightAnchorPane.getChildren().add(FXMLLoader
                //         .load(getClass().getClassLoader().getResource("views/SingleDocParameterPanel.fxml")));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleSingleDocButtonClicked(ActionEvent event) {
        model.prechecked = Prechecked.NONE;
        showRightPanel();
    }

    public void handlePOSButtonClicked(ActionEvent event) throws IOException {
        model.prechecked = Prechecked.POS;
        showRightPanel();
    }

    public void handleNERButtonClicked(ActionEvent event) throws IOException {
        model.prechecked = Prechecked.NER;
        showRightPanel();
    }

    public void onWordFrequencyButtonClicked(ActionEvent event) {
        model.prechecked = Prechecked.WORD_FREQ;
        showRightPanel();
    }

    public void onWordCloudButtonClicked(ActionEvent event) {
        model.prechecked = Prechecked.WORD_CLOUD;
        showRightPanel();
    }

    public void loadHTMLFileToDisplay() throws MalformedURLException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Open Text File");

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
        fileChooser.getExtensionFilters().add(extFilter);

        if (openedHTMLFile != null) {
            fileChooser.setInitialDirectory(openedHTMLFile.getParentFile());
        }

        openedHTMLFile = fileChooser.showOpenDialog(null);

        URL fileURL = openedHTMLFile.toURI().toURL();
        System.out.println(fileURL);
        this.webEngine.load(fileURL.toString());
    }

    public void closeViewWindow() {
        this.webEngine.load(null);
        this.rightAnchorPane.getChildren().clear();
    }


    public void saveHTMLPageAs() {
        if (HTMLHelper.isDocumentNull(this.webEngine.getDocument())) {
            showNoPageLoadedAlert();
        } else {

            FileChooser fileChooser = new FileChooser();

            // Set extension filter
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
            fileChooser.getExtensionFilters().add(extFilter);

            // Show save file dialog
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                HTMLHelper.saveDocument(this.webEngine.getDocument(), file);
            }
        }
    }


    public void showExtractedHTML() throws Exception {
        if (HTMLHelper.isDocumentNull(this.webEngine.getDocument())) {
            showNoPageLoadedAlert();
        } else {
            URI uri = new URI(this.webEngine.getDocument().getDocumentURI());
            String extractedHTMLString = HTMLHelper.getExtractedHTMLString(uri.toURL());
            this.webEngine.loadContent(extractedHTMLString);
        }
    }
}
