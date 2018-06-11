package controllers;

import com.sun.deploy.util.FXLoader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.DataModel;

import java.io.IOException;

public class SplashScreenController {
    @FXML
    private StackPane rootStackPane;

    private DataModel dataModel;

    public void initialize() {
        new SplashScreen().start();
    }

    public void initDataModel(DataModel model) {
        this.dataModel = model;
    }

    class SplashScreen extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);

                /*
                JavaFX uses only one thread to update its GUI. You can't change the GUI from other threads.
                 */

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        // Parent root = null;

                        MainController mainController = new MainController();
                        mainController.initDataModel(dataModel);

                        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/Main.fxml"));
                        loader.setController(mainController);

                        Parent root = null;

                        try {
                            root = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        root.getStylesheets().add(getClass().getClassLoader().getResource("css/materialfx-v0_3.css").toString());

                        Stage stage = new Stage();
                        stage.setScene(new Scene(root));

                        rootStackPane.getScene().getWindow().hide();  // Hide the splash screen after starting
                        stage.show();
                    }
                });

            } catch (InterruptedException e) {
                System.out.println("After sleeping...");
            }
        }
    }
}
