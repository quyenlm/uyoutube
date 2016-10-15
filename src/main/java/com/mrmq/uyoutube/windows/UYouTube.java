package com.mrmq.uyoutube.windows;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UYouTube extends Application {

    public static void main(String[] args) {
        Application.launch(UYouTube.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        showLogin(stage);
    }

    private void showLogin(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../../../../fxml/fxml_login.fxml"));

        stage.setTitle("Login");
        stage.setScene(new Scene(root, 300, 275));
        stage.show();
    }

    private void showMain(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../../../../fxml/fxml_main.fxml"));

        stage.setTitle("uYouTube 1.0");
        stage.setScene(new Scene(root, 1024, 768));
        stage.show();
    }
}
