package com.mrmq.uyoutube.windows;

import com.google.api.services.youtube.model.Video;
import com.google.common.base.Function;
import com.mrmq.uyoutube.AppStartup;
import com.mrmq.uyoutube.Context;
import com.mrmq.uyoutube.beans.VideoDirectory;
import com.mrmq.uyoutube.config.Config;
import com.mrmq.uyoutube.data.VideoSearch;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UYouTubeUploadController {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @FXML private Text txtMessage;
    @FXML private TextField txtChannelId;
    @FXML private Label lbChannelVideoValue;
    @FXML private Button btnUploadVideo;
    @FXML private Button btnRefresh;

    @FXML private Label lbNewVideos;
    @FXML private ListView lvNewVideos;
    @FXML private Label lbUploaded;
    @FXML private ListView lvUploaded;
    @FXML private ProgressBar progressBarUpload;
    private Task searchVideosWorker;

    private Map<String, Video> newVideos = new ConcurrentHashMap<String, Video>();

    @FXML protected void handleButtonAction(ActionEvent event) {
        try {
            if(event.getSource() == btnRefresh) {
                handleRefreshButtonAction(event);
            } else if(event.getSource() == btnUploadVideo) {
                handleUploadButtonAction(event);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void handleRefreshButtonAction(ActionEvent event) throws IOException {
        lvNewVideos.getItems().clear();
        lvUploaded.getItems().clear();
        newVideos.clear();
        txtMessage.setText("");
        lbChannelVideoValue.setText("");
        disableControls(true);

        String channelId = txtChannelId.getText().trim();
        if(channelId.length() == 0) {
            txtMessage.setText("Channel id invalid: " + channelId);
            return;
        }

        //load downloaded video in directory
        VideoDirectory channelDir = new VideoDirectory(Config.getInstance().getDownloadPath() + channelId);
        final Map<String, Video> downloadedVideos = channelDir.loadInfo();
        final Map<String, Video> uploadedVideos = Context.getYouTubeService().getVideos();
        for (Video video: downloadedVideos.values()) {
            if (!uploadedVideos.containsKey(video.getId())) {
                newVideos.put(video.getId(), video);
            }
        }

        if(newVideos.size() > 0)
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    for (Video video: newVideos.values()) {
                        lvNewVideos.getItems().add(video.getSnippet().getTitle());
                    }
                    for (Video video : downloadedVideos.values()) {
                        if(uploadedVideos.containsKey(video.getId()))
                            lvUploaded.getItems().add(video.getSnippet().getTitle());
                    }

                    lbNewVideos.setText("New videos: " + newVideos.size());
                    lbNewVideos.setText("New videos: " + newVideos.size());
                    if(newVideos.size() > 0)
                        btnUploadVideo.setDisable(false);
                    else
                        btnUploadVideo.setDisable(true);
                }
            });
    }

    private void handleUploadButtonAction(ActionEvent event) {
        try {
            Map<String, Video> toDownload = newVideos;
            Context.getYouTubeService().upload(toDownload);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void disableControls(boolean disable){
        btnRefresh.setDisable(disable);
        btnUploadVideo.setDisable(disable);
    }
}