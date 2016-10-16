package com.mrmq.uyoutube.windows;

import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.AppStartup;
import com.mrmq.uyoutube.data.VideoSearch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UYouTubeReupController {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @FXML private Text txtMessage;
    @FXML private TextField txtChannelId;
    @FXML private Button btnDownloadVideo;
    @FXML private Button btnReupVideo;

    @FXML private ListView lvVideos;
    @FXML private ListView lvDownloaded;

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

        List<Video> search = VideoSearch.search(null, txtChannelId.getText());
        if(search != null)
            for(Video video : search)
                lvVideos.getItems().add(video.getSnippet().getTitle());
        else
            txtMessage.setText("Not found videos in channel: " + txtChannelId.getText());
    }

}