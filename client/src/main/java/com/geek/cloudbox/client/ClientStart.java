package com.geek.cloudbox.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ClientStart extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("CloudBox");
        Scene scene = new Scene(root, 400, 420);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Network.start();
        launch(args);
    }
}
