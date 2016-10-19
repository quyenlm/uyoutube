package com.mrmq.uyoutube.windows;

import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.AppStartup;
import com.mrmq.uyoutube.beans.VideoDirectory;
import com.mrmq.uyoutube.config.Config;
import com.mrmq.uyoutube.config.Context;
import com.mrmq.uyoutube.data.VideoSearch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UYouTubeReupController {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @FXML private Text txtMessage;
    @FXML private TextField txtChannelId;
    @FXML private Label lbChannelVideoValue;
    @FXML private Button btnDownloadVideo;
    @FXML private Button btnReupVideo;

    @FXML private ListView lvVideos;
    @FXML private ListView lvDownloaded;
    private Map<String, Video> newVideos = new ConcurrentHashMap<String, Video>();

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        try {
            if(event.getSource() == btnDownloadVideo) {
                search();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void search() {
        lvVideos.getItems().clear();
        lvDownloaded.getItems().clear();
        newVideos.clear();
        txtMessage.setText("");
        lbChannelVideoValue.setText("");

        String channelId = txtChannelId.getText().trim();
        if(channelId.length() == 0) {
            txtMessage.setText("Channel id invalid: " + channelId);
            return;
        }

        try {
            //load downloaded video in directory
            VideoDirectory channelDir = new VideoDirectory(Config.getDownloadPath() + channelId);
            Map<String, Video> downloadedVideos = channelDir.loadInfo();
            if(downloadedVideos.size() > 0)
                for (Video video: downloadedVideos.values()) {
                    lvDownloaded.getItems().add(video.getSnippet().getTitle());
                }

            int total = 0;
            int downloaded = 0;

            //load video in channel from youtube
            List<Video> search = VideoSearch.search(null, channelId);
            if(search != null) {
                total = search.size();

                for (Video video : search) {
                    if(!downloadedVideos.containsKey(video.getId())) {
                        lvVideos.getItems().add(video.getSnippet().getTitle());
                        newVideos.put(video.getId(), video);
                    } else
                        downloaded++;
                }
            }
            else
                txtMessage.setText("Not found videos in channel: " + channelId);

            lbChannelVideoValue.setText(String.format("There are %d videos, downloaded: %d, remain: %d", total, downloaded, total - downloaded));
        } catch (Exception e) {
            txtMessage.setText(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    private void downloadVideos() {
        if(newVideos.size() > 0)
            for (Video video: newVideos.values()) {
                Context.getDownloadService().add(video);
            }
    }
}