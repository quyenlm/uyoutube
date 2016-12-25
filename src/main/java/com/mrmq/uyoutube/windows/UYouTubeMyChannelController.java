package com.mrmq.uyoutube.windows;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.common.collect.Lists;
import com.mrmq.uyoutube.Context;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class UYouTubeMyChannelController implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(UYouTubeMyChannelController.class);

    @FXML private ListView lvVideos;
    @FXML private Label lbChannelIdValue;
    @FXML private Label lbChannelNameValue;
    @FXML private Label lbChannelVideoValue;
    @FXML private TextArea txtTags;
    @FXML private Button btnSave;
    @FXML private Button btnRefresh;

    @FXML private ProgressBar progressBar;
    private Task worker;
    private Map<String, Video> videos;

    public UYouTubeMyChannelController() {
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

            if(event.getSource() == btnRefresh) {
                loadChannel();
            } else if(event.getSource() == btnSave) {
                saveTags();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void loadChannel() {
        lbChannelIdValue.setText("");
        lbChannelNameValue.setText("");
        lbChannelVideoValue.setText("");

        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        worker = createLoadChannelWorker();
        progressBar.progressProperty().bind(worker.progressProperty());

        new Thread(worker).start();
    }

    private void saveTags() {
        String tags = txtTags.getText();

        if(!StringUtils.isEmpty(tags)) {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            worker = createUpdateVideosWorker();
            progressBar.progressProperty().bind(worker.progressProperty());
            new Thread(worker).start();
        }
    }

    public Task createLoadChannelWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                int total = 100;
                updateMessage("5 percents");
                updateProgress(5, 100);
                final Channel channel = Context.getYouTubeService().getMyChannel();
                videos = Context.getYouTubeService().getMyUpload();

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

    public Task createUpdateVideosWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                int total = 100;
                updateMessage("2 percents");
                updateProgress(2, 100);

                List<String> tags = Lists.asList("", txtTags.getText().split(","));
                int count = 0;

                if(videos != null)
                    for(Video video : videos.values()) {
                        if(video.getSnippet() == null)
                            video.setSnippet(new VideoSnippet());
                        video.getSnippet().setTags(tags);
                        Context.getYouTubeService().updateVideo(video);
                        updateProgress(100*count/videos.size(), 100);
                        Thread.sleep(System.currentTimeMillis() % 1000);
                    }

                updateMessage("100 percents");
                updateProgress(100, 100);
                return true;
            }
        };
    }
}