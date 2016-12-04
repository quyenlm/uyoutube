package com.mrmq.uyoutube.windows;

import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.beans.VideoDirectory;
import com.mrmq.uyoutube.config.Config;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UYouTubeToolController {
    private static final Logger logger = LoggerFactory.getLogger(UYouTubeToolController.class);

    @FXML private Text txtMessage;
    @FXML private TextField txtChannelId;
    @FXML private Label lbChannelVideoValue;
    @FXML private Button btnMergeVideo;
    @FXML private Button btnRefresh;

    @FXML private Label lbNewVideos;
    @FXML private ListView lvNewVideos;
    @FXML private Label lbDownloaded;
    @FXML private ListView lvDownloaded;
    @FXML private ProgressBar progressBarMerged;
    private Task searchVideosWorker;

    private Map<String, Video> newVideos = new ConcurrentHashMap<String, Video>();

    @FXML protected void handleButtonAction(ActionEvent event) {
        try {
            if(event.getSource() == btnRefresh) {
                handleRefreshButtonAction(event);
            } else if(event.getSource() == btnMergeVideo) {
                //handleDownloadButtonAction(event);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void handleRefreshButtonAction(ActionEvent event) {
        try {
            String channelId = txtChannelId.getText().trim();
            if(channelId.length() == 0)
                return;

            //load downloaded video in directory
            VideoDirectory channelDir = new VideoDirectory(Config.getInstance().getDownloadPath() + channelId);
            final Map<String, Video> downloadedVideos = channelDir.loadNotMergedVideos();

            lbChannelVideoValue.setText("Not Merged videos: " + downloadedVideos.size());
            for (Video video : downloadedVideos.values()) {
                //ffmpeg -i JTcweiLfueE.mp4 -i JTcweiLfueE.webm -c:a aac -c:v libx264 -strict -2 -c:s copy JTcweiLfueE_Merged.mp4
                logger.info("ffmpeg -i {}.mp4 -i {}.webm -c:a aac -c:v libx264 -strict -2 -c:s copy {}_Merged.mp4", video.getId(), video.getId(), video.getId());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}