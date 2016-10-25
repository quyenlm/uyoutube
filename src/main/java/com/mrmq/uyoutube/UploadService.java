package com.mrmq.uyoutube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.beans.ErrorCode;
import com.mrmq.uyoutube.beans.Result;
import com.mrmq.uyoutube.config.Config;
import com.mrmq.uyoutube.data.UploadVideo;
import com.mrmq.uyoutube.helper.FileHelper;

import java.util.concurrent.TimeUnit;

public class UploadService extends Service {
    public UploadService(YouTube youtube) {
        super(youtube);
    }

    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
                Video video = queues.poll(1000, TimeUnit.MILLISECONDS);
                if(video != null) {
                    logger.info("start upload video: {}", video);
                    Result<Video> result = UploadVideo.upload(youtube, video.getSnippet().getTitle(),
                            video.getSnippet().getDescription(),
                            FileHelper.createVideoUploadFile(Config.getInstance().getDownloadPath() + video.getSnippet().getChannelId(), video.getId()),
                            video.getSnippet().getTags());

                    if(result.getErrorCode().equals(ErrorCode.SUCCESS)) {
                        //Save upload trace to ini file
                        if(result.getValue() != null && getYouTubeService() != null)
                            getYouTubeService().addVideoTrace(video.getId(), result.getValue());
                    }

                    logger.info("end upload video: {}", video);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
