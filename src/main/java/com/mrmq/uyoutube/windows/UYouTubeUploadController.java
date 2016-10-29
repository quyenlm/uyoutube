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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UYouTubeUploadController {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @FXML private Text txtMessage;
    @FXML private TextField txtChannelId;
    @FXML private Label lbChannelVideoValue;
    @FXML private Button btnDownloadVideo;
    @FXML private Button btnRefresh;
    @FXML private Button btnReupVideo;

    @FXML private Label lbNewVideos;
    @FXML private ListView lvNewVideos;
    @FXML private Label lbDownloaded;
    @FXML private ListView lvDownloaded;
    @FXML private ProgressBar progressBarDownload;
    @FXML private ProgressBar progressBarReup;
    private Task searchVideosWorker;

    private Map<String, Video> newVideos = new ConcurrentHashMap<String, Video>();

    @FXML protected void handleButtonAction(ActionEvent event) {
        try {
            if(event.getSource() == btnRefresh) {
                handleRefreshButtonAction(event);
            } else if(event.getSource() == btnReupVideo) {
                handleReupButtonAction(event);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void handleRefreshButtonAction(ActionEvent event) {
        lvNewVideos.getItems().clear();
        lvDownloaded.getItems().clear();
        newVideos.clear();
        txtMessage.setText("");
        lbChannelVideoValue.setText("");
        disableControls(true);

        String channelId = txtChannelId.getText().trim();
        if(channelId.length() == 0) {
            txtMessage.setText("Channel id invalid: " + channelId);
            return;
        }

        try {
            progressBarDownload.progressProperty().unbind();
            progressBarDownload.setProgress(0);
            searchVideosWorker = createSearchVideosWorker(channelId);
            progressBarDownload.progressProperty().bind(searchVideosWorker.progressProperty());

            Thread worker = new Thread(searchVideosWorker);
            worker.start();
        } catch (Exception e) {
            txtMessage.setText(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    protected void handleReupButtonAction(ActionEvent event) {
        try {
            Map<String, Video> toDownload = newVideos;
            Context.getYouTubeService().upload(toDownload);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Task createSearchVideosWorker(final String channelId) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                //load downloaded video in directory
                VideoDirectory channelDir = new VideoDirectory(Config.getInstance().getDownloadPath() + channelId);
                final Map<String, Video> downloadedVideos = channelDir.loadInfo();
                if(downloadedVideos.size() > 0)
                    Platform.runLater(new Runnable(){
                        @Override
                        public void run() {
                            lbDownloaded.setText("Downloaded videos: " + downloadedVideos.size());
                            for (Video video: downloadedVideos.values()) {
                                lvDownloaded.getItems().add(video.getSnippet().getTitle());
                            }
                        }
                    });

                //load video in channel from youtube
                final List<Video> search = VideoSearch.search(null, channelId, Config.getInstance().getApiKey(), new Function<Integer, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Integer integer) {
                        updateProgress(integer, 100);
                        return null;
                    }
                });

                final AtomicInteger downloaded = new AtomicInteger(0);
                if(search != null) {
                    for (final Video video : search) {
                        if(!downloadedVideos.containsKey(video.getId())) {
                            Platform.runLater(new Runnable(){
                                @Override
                                public void run() {
                                    lvNewVideos.getItems().add(video.getSnippet().getTitle());
                                }
                            });
                            newVideos.put(video.getId(), video);
                        } else
                            downloaded.incrementAndGet();
                    }
                }
                else
                    txtMessage.setText("Not found videos in channel: " + channelId);

                updateProgress(100, 100);

                Platform.runLater(new Runnable(){
                    @Override
                    public void run() {
                        lbNewVideos.setText("News videos: " + (search.size() - downloaded.get()));
                        lbDownloaded.setText("Downloaded videos: " + downloaded.get());
                        lbChannelVideoValue.setText(String.format("There are %d videos, downloaded: %d, remain: %d", search.size(), downloaded.get(), search.size() - downloaded.get()));
                        disableControls(false);
                        if(newVideos.size() > 0)
                            btnDownloadVideo.setDisable(false);
                        else
                            btnDownloadVideo.setDisable(true);
                    }
                });

                return true;
            }
        };
    }

    private void disableControls(boolean disable){
        btnRefresh.setDisable(disable);
        btnDownloadVideo.setDisable(disable);
    }
}