package com.mrmq.uyoutube.windows;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.common.collect.Lists;
import com.mrmq.uyoutube.Context;
import com.mrmq.uyoutube.config.ChannelSetting;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
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

    @FXML private Button btnSave;
    @FXML private Button btnRefresh;
    @FXML private TextField txtTitleOld;
    @FXML private TextField txtTitleNews;
    @FXML private TextField txtDescOld;
    @FXML private TextField txtDescNews;
    @FXML private TextArea txtDescAppend;
    @FXML private TextArea txtTags;
    @FXML private Text txtMessage;

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
        btnRefresh.setDisable(true);
        lbChannelIdValue.setText("");
        lbChannelNameValue.setText("");
        lbChannelVideoValue.setText("");
        lvVideos.getItems().clear();
        if(videos != null)
            videos.clear();


        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        worker = createLoadChannelWorker();
        progressBar.progressProperty().bind(worker.progressProperty());

        new Thread(worker).start();
    }

    private void saveTags() {
        String msg = "";
        btnSave.setDisable(true);
        try {
            progressBar.progressProperty().unbind();
            progressBar.setProgress(0);
            worker = createUpdateVideosWorker();
            progressBar.progressProperty().bind(worker.progressProperty());
            new Thread(worker).start();
        } catch (Exception e) {
            msg = e.getMessage();
            logger.error(e.getMessage(), e);
        }

        txtMessage.setText(msg);
    }

    public Task createLoadChannelWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    int total = 100;
                    updateMessage("5 percents");
                    updateProgress(5, 100);
                    final Channel channel = Context.getYouTubeService().getMyChannel();
                    videos = Context.getYouTubeService().getMyUpload();

                    if (channel != null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                lbChannelIdValue.setText(channel.getId());
                                if (channel.getSnippet() != null)
                                    lbChannelNameValue.setText(channel.getSnippet().getTitle());
                                lbChannelVideoValue.setText(String.valueOf(videos.size()));
                            }
                        });
                    }

                    updateMessage("80 percents");
                    updateProgress(80, 100);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            for (Video video : videos.values()) {
                                lvVideos.getItems().add(String.format("%s - %s - %s", video.getId(), video.getSnippet().getTitle(), video.getSnippet().getDescription()));
                            }
                        }
                    });

                    updateMessage("100 percents");
                    updateProgress(100, 100);
                } finally {
                    btnRefresh.setDisable(false);
                }
                return true;
            }
        };
    }

    public Task createUpdateVideosWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    int total = 100;
                    updateMessage("2 percents");
                    updateProgress(2, 100);

                    String oldTitle = txtTitleOld.getText();
                    String newTitle = txtTitleNews.getText();
                    String oldDesc = txtDescOld.getText();
                    String newDesc = txtDescNews.getText();
                    String descAppend = txtDescAppend.getText();
                    String tags = txtTags.getText();

                    if (StringUtils.isEmpty(oldTitle)
                            && StringUtils.isEmpty(newTitle)
                            && StringUtils.isEmpty(oldDesc)
                            && StringUtils.isEmpty(oldTitle)
                            && StringUtils.isEmpty(newDesc)
                            && StringUtils.isEmpty(descAppend)
                            && StringUtils.isEmpty(tags)) {

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                txtMessage.setText("At least 1 field is required");
                            }
                        });
                        return false;
                    }

                    ChannelSetting setting = Context.getYouTubeService().makeChannelSetting(oldTitle, newTitle, oldDesc, newDesc, descAppend, tags);

                    int count = 0;
                    if (videos != null) {
                        for (Video video : videos.values()) {
                            count++;
                            Context.getYouTubeService().updateVideo(video, setting);
                            updateProgress(100 * count / videos.size(), 100);
                            Thread.sleep(System.currentTimeMillis() % 1000);
                        }
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            loadChannel();
                            txtMessage.setText("Update all videos OK");
                        }
                    });

                    updateMessage("100 percents");
                    updateProgress(100, 100);
                    return true;
                } finally {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnSave.setDisable(false);
                        }
                    });
                }
            }
        };
    }
}