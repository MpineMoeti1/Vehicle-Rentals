package com.example.demo1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Ensure the FXML file path is correct and it's in the resources directory
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/demo1/login.fxml")));

        if (root == null) {
            System.err.println("Failed to load FXML file: /com/example/demo1/login.fxml");
            return;
        }

        primaryStage.setTitle("Vehicle Rental System - Login");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        try {
            DatabaseConnector.closeAllConnections();
        } catch (Exception e) {
            System.err.println("Failed to close database connections: " + e.getMessage());
        }
        super.stop();
    }
}
