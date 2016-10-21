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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    for (Video video: Context.getYouTubeService().getMyUpload().values()) {
                        lvVideos.getItems().add(String.format("%s - %s - %s", video.getId(), video.getSnippet().getTitle(), video.getSnippet().getDescription()));
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }

            }
        });
    }


    private void showProgressBar(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../../../../fxml/fxml_progress_bar_layout.fxml"));
        AnchorPane root = (AnchorPane) loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    private Task loadChannelWorker;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private void onButtonClick() {
        //progressBar = new ProgressBar(0);
        progressBar.setProgress(0);
        loadChannelWorker = createWorker();

        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(loadChannelWorker.progressProperty());

        new Thread(loadChannelWorker).start();
    }

    public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                int j = 0;
                for (int i = 0; i < 101; i++) {
                    j++;
                    Thread.sleep(20);
                    updateMessage("2000 milliseconds");
                    updateProgress(j, 100);

                    System.out.println(progressBar.getProgress());
                }
                return true;
            }
        };
    }
}