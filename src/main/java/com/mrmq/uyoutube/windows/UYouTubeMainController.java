package com.mrmq.uyoutube.windows;

import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.AppStartup;
import com.mrmq.uyoutube.YouTubeService;
import com.mrmq.uyoutube.config.Config;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UYouTubeMainController {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @FXML private Text actiontarget;
    @FXML private Button btnShowReup;
    @FXML private ListView lvVideos;

    ExecutorService executor = Executors.newCachedThreadPool();
    public UYouTubeMainController() {
        super();

        loadChannel();
    }

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        try {
            Stage stage = null;
            Parent root;
            Scene scene = null;

            if(event.getSource() == btnShowReup) {
                stage = (Stage) btnShowReup.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("../../../../fxml/fxml_reup.fxml"));
                scene = new Scene(root, 1024, 768);
            }

            //create a new scene with root and set the stage
            if(stage != null) {
                stage.setScene(scene);
                stage.show();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void loadChannel() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    for (Video video: Config.uouTubeService.getMyUpload().values()) {
                        lvVideos.getItems().add(video.getSnippet().getTitle());
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }

            }
        });
    }
}