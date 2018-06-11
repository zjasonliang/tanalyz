import controllers.SplashScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.DataModel;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Parent root = FXMLLoader.load(getClass().getResource("views/Main.fxml"));

        DataModel dataModel = new DataModel();

        SplashScreenController splashScreenController = new SplashScreenController();
        splashScreenController.initDataModel(dataModel);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/SplashScreen.fxml"));
        loader.setController(splashScreenController);
        Parent root = loader.load();
        root.getStylesheets().add(getClass().getResource("css/materialfx-v0_3.css").toString());



        // Parent root = FXMLLoader.load(getClass().getResource("views/SplashScreen.fxml"));
        // root.getStylesheets().add(getClass().getResource("css/materialfx-v0_3.css").toString());

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
