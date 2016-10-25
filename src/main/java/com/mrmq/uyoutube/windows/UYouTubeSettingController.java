package com.mrmq.uyoutube.windows;

import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.AppStartup;
import com.mrmq.uyoutube.Context;
import com.mrmq.uyoutube.beans.ScreenSetting;
import com.mrmq.uyoutube.config.Config;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class UYouTubeSettingController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @FXML private Button btnSave;
    @FXML private TextField txtTitleOld;
    @FXML private TextField txtTitleNews;
    @FXML private TextField txtDescOld;
    @FXML private TextField txtDescNews;
    @FXML private TextArea txtDescAppend;

    public UYouTubeSettingController() {
        super();
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            txtTitleOld.setText(Config.getInstance().getOldTitleReplace());
            txtTitleNews.setText(Config.getInstance().getNewTitleReplace());
            txtDescOld.setText(Config.getInstance().getOldDescReplace());
            txtDescNews.setText(Config.getInstance().getNewDescReplace());
            txtDescAppend.setText(Config.getInstance().getDescAppend());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @FXML protected void handleButtonAction(ActionEvent event) {
        try {
            Stage stage = null;
            Parent root;
            Scene scene = null;

            if(event.getSource() == btnSave) {
                Config.getInstance().setOldTitleReplace(txtTitleOld.getText());
                Config.getInstance().setNewTitleReplace(txtTitleNews.getText());
                Config.getInstance().setOldDescReplace(txtDescOld.getText());
                Config.getInstance().setNewDescReplace(txtDescNews.getText());
                Config.getInstance().setDescAppend(txtDescAppend.getText());

                stage = (Stage) btnSave.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("../../../../fxml/fxml_main.fxml"));
                ScreenSetting setting = Config.getScreenSetting().get(ScreenSetting.SCREEN_MAIN);
                scene = new Scene(root, setting.getWidth(), setting.getHeight());
            }

            //create a new scene with root and set the stage
            if(stage != null) {
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}