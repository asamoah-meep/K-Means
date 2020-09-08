package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ClassLoader loader = getClass().getClassLoader();
        Parent root = FXMLLoader.load(loader.getResource("sample.fxml"));

        Scene s = new Scene(root, 500, 500);
        s.getStylesheets().add("styles.css");

        primaryStage.setTitle("K-Means Parameters");
        primaryStage.setScene(s);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
