package com.mrmq.uyoutube.windows;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.AppStartup;
import com.mrmq.uyoutube.Context;
import com.mrmq.uyoutube.beans.ScreenSetting;
import com.mrmq.uyoutube.config.Config;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class UYouTubeMainController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @FXML private Text actiontarget;
    @FXML private Button btnShowReup;
    @FXML private ListView lvVideos;
    @FXML private Label lbChannelIdValue;
    @FXML private Label lbChannelNameValue;
    @FXML private Label lbChannelVideoValue;

    @FXML private Button btnSettings;

    @FXML private ProgressBar progressBar;
    private Task loadChannelWorker;

    public UYouTubeMainController() {
        super();
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loadChannel();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        try {

            Stage stage = null;
            Parent root;
            Scene scene = null;

            if(event.getSource() == btnShowReup) {
                stage = (Stage) btnShowReup.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("../../../../fxml/fxml_reup.fxml"));
                ScreenSetting setting = Config.getScreenSetting().get(ScreenSetting.SCREEN_MAIN);
                scene = new Scene(root, setting.getWidth(), setting.getHeight());
            } else if(event.getSource() == btnSettings) {
                stage = (Stage) btnSettings.getScene().getWindow();
                root = FXMLLoader.load(getClass().getResource("../../../../fxml/fxml_settings.fxml"));
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

    private void loadChannel() {
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        loadChannelWorker = createLoadChannelWorker();
        progressBar.progressProperty().bind(loadChannelWorker.progressProperty());

        new Thread(loadChannelWorker).start();
    }

    public Task createLoadChannelWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                int total = 100;
                updateMessage("10 percents");
                updateProgress(20, 100);
                final Channel channel = Context.getYouTubeService().getMyChannel();
                final Map<String, Video> videos = Context.getYouTubeService().getMyUpload();

                if(channel != null) {
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run() {
                            lbChannelIdValue.setText(channel.getId());
                            if(channel.getSnippet() != null)
                                lbChannelNameValue.setText(channel.getSnippet().getTitle());
                            lbChannelVideoValue.setText(String.valueOf(videos.size()));
                        }
                    });
                }

                updateMessage("80 percents");
                updateProgress(80, 100);
                for (Video video: videos.values()) {
                    lvVideos.getItems().add(String.format("%s - %s - %s", video.getId(), video.getSnippet().getTitle(), video.getSnippet().getDescription()));
                }

                updateMessage("100 percents");
                updateProgress(100, 100);
                return true;
            }
        };
    }
}