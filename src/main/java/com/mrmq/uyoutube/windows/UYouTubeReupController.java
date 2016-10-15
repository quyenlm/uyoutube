package com.mrmq.uyoutube.windows;

import com.mrmq.uyoutube.AppStartup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UYouTubeReupController {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @FXML private Text txtMessage;
    @FXML private Button btnDownloadVideo;
    @FXML private Button btnReupVideo;

    @FXML private ListView lvVideos;
    @FXML private ListView lvDownloaded;

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        try {
            if(event.getSource() == btnDownloadVideo) {
                for(int i = 0; i < 10; i++)
                    lvVideos.getItems().add("Videos " + 1);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}