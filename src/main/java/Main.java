import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("views/Main.fxml"));
        root.getStylesheets().add(getClass().getResource("css/materialfx-v0_3.css").toString());
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
