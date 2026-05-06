package org.csc311.capstone;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        scene.getStylesheets().add(HelloApplication.class.getResource("styles.css").toExternalForm());

        stage.setMinWidth(1920);
        stage.setMinHeight(1080);
        stage.setTitle("Smart Student Management System");
        stage.setScene(scene);
        stage.show();
    }
}