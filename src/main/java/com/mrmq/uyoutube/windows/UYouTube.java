package com.mrmq.uyoutube.windows;

import com.mrmq.uyoutube.beans.ScreenSetting;
import com.mrmq.uyoutube.config.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class UYouTube extends Application {

    public static void main(String[] args) {
        String[] xmlConfigs = new String[]{"classpath:spring-uyoutube-context.xml"};
        ApplicationContext ctx = new ClassPathXmlApplicationContext(xmlConfigs);
        Config.init();
        Application.launch(UYouTube.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        showLogin(stage);
    }

    private void showLogin(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../../../../fxml/fxml_login.fxml"));

        stage.setTitle(Config.getApiKey());
        ScreenSetting setting = Config.getScreenSetting().get(ScreenSetting.SCREEN_LOGIN);
        stage.setScene(new Scene(root, setting.getWidth(), setting.getHeight()));
        stage.show();
    }

    private void showMain(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../../../../fxml/fxml_main.fxml"));

        stage.setTitle("uYouTube 1.0");
        stage.setScene(new Scene(root, 1024, 768));
        stage.show();
    }
}
