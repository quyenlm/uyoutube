package com.mrmq.uyoutube.windows;

import com.mrmq.uyoutube.beans.ScreenSetting;
import com.mrmq.uyoutube.config.ChannelSetting;
import com.mrmq.uyoutube.config.Config;
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
import java.util.ResourceBundle;

public class UYouTubeSettingController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(UYouTubeSettingController.class);

    @FXML private Button btnRefresh;
    @FXML private Button btnSave;
    @FXML private TextField txtChannelId;
    @FXML private TextField txtTitleOld;
    @FXML private TextField txtTitleNews;
    @FXML private TextField txtDescOld;
    @FXML private TextField txtDescNews;
    @FXML private TextArea txtDescAppend;
    @FXML private Text txtMessage;

    public UYouTubeSettingController() {
        super();
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            init(txtChannelId.getText().trim());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @FXML protected void handleButtonAction(ActionEvent event) {
        try {
            if(event.getSource() == btnRefresh) {
                init(txtChannelId.getText().trim());
            } else if(event.getSource() == btnSave) {
                save(txtChannelId.getText().trim());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void init(String channelId) {
        if(channelId == null || channelId.length() == 0) {
            logger.error("Invalid channelId: {}", channelId);
            txtMessage.setText("Invalid channelId: " + channelId);
            return;
        }

        ChannelSetting setting = Config.getInstance().getChannelSettings(channelId);
        if(setting == null)
            setting = ChannelSetting.load(channelId);

        if(setting != null) {
            txtTitleOld.setText(setting.getOldTitleReplace());
            txtTitleNews.setText(setting.getNewTitleReplace());
            txtDescOld.setText(setting.getOldDescReplace());
            txtDescNews.setText(setting.getNewDescReplace());
            txtDescAppend.setText(setting.getDescAppend());
        }
    }

    private void save(String channelId) {
        if(channelId == null || channelId.length() == 0) {
            logger.error("Invalid channelId: {}", channelId);
            txtMessage.setText("Invalid channelId: " + channelId);
            return;
        }

        ChannelSetting setting = Config.getInstance().getChannelSettings(channelId);
        if(setting == null)
            setting = new ChannelSetting();
        setting.setChannelId(channelId);
        setting.setOldTitleReplace(txtTitleOld.getText());
        setting.setNewTitleReplace(txtTitleNews.getText());
        setting.setOldDescReplace(txtDescOld.getText());
        setting.setNewDescReplace(txtDescNews.getText());
        setting.setDescAppend(txtDescAppend.getText());
        ChannelSetting.save(setting);
        Config.getInstance().setChannelSettings(setting);
        logger.info("Saved setting, channelId: {}", channelId);
    }
}