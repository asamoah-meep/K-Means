package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage primaryStage;
    private static ClassLoader loader;

    @Override
    public void start(Stage p) throws IOException{
        loader = getClass().getClassLoader();
        Main.primaryStage = p;
        Parent root = FXMLLoader.load(loader.getResource("input.fxml"));

        Scene s = new Scene(root, 500, 500);
        s.getStylesheets().add("styles.css");

        primaryStage.setTitle("K-Means Parameters");
        primaryStage.setScene(s);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage(){
        return primaryStage;
    }

    public static void changeScene() throws IOException {
        Parent root = FXMLLoader.load(loader.getResource("loader.fxml"));
        Scene s = new Scene(root, 500, 500);
        s.getStylesheets().add("styles.css");
        primaryStage.close();
        primaryStage.setTitle("Processing...");
        primaryStage.setScene(s);
        primaryStage.show();

    }
}
