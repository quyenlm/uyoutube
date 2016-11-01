package com.mrmq.uyoutube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.beans.ErrorCode;
import com.mrmq.uyoutube.beans.HandleEvent;
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
                    Video uploadVideo = FileHelper.makeUploadVideo(video);
                    Result<Video> result = UploadVideo.upload(youtube, uploadVideo.getSnippet().getTitle(),
                            uploadVideo.getSnippet().getDescription(),
                            FileHelper.createVideoUploadFile(Config.getInstance().getDownloadPath() + video.getSnippet().getChannelId(), video.getId()),
                            Config.getInstance().getDefaultTags());

                    if(result.getErrorCode().equals(ErrorCode.SUCCESS)) {
                        //Save upload trace to ini file
                        if(result.getValue() != null && Context.getYouTubeService() != null)
                            Context.getYouTubeService().addVideoTrace(video.getId(), result.getValue());
                    }

                    completedTask.incrementAndGet();
                    logger.info("end upload video: {}", video);

                    HandleEvent event = new HandleEvent(totalTask.get(), completedTask.get());
                    event.setCookies(video);
                    onEvent(event);

                    if(result.getErrorCode().equals(ErrorCode.SUCCESS))
                        Thread.sleep(22000);
                    if(totalTask.get() == completedTask.get()) {
                        completedTask.set(0);
                        totalTask.set(0);
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                completedTask.incrementAndGet();
                onEvent(new HandleEvent(totalTask.get(), completedTask.get()));
            }
        }
    }
}
