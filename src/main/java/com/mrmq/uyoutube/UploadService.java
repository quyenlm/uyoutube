package com.mrmq.uyoutube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.common.base.Preconditions;
import com.mrmq.uyoutube.beans.ErrorCode;
import com.mrmq.uyoutube.beans.HandleEvent;
import com.mrmq.uyoutube.beans.Result;
import com.mrmq.uyoutube.config.ChannelSetting;
import com.mrmq.uyoutube.config.Config;
import com.mrmq.uyoutube.data.UploadVideo;
import com.mrmq.uyoutube.helper.FileHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class UploadService extends Service {
    public UploadService(YouTube youtube) {
        super(youtube);
    }

    @Override
    public void process() throws InterruptedException, IOException {
        Video video = queues.poll(1000, TimeUnit.MILLISECONDS);
        if(video != null) {
            logger.info("start upload video: {}", video);
            ChannelSetting setting = Config.getInstance().getChannelSettings(video.getSnippet().getChannelId());
            Preconditions.checkNotNull(setting, "setting is null, channelId: " + video.getSnippet().getChannelId());

            Video uploadVideo = FileHelper.makeUploadVideo(video, setting);
            Result<Video> result = UploadVideo.upload(youtube, FileHelper.createVideoUploadFile(Config.getInstance().getDownloadPath() + video.getSnippet().getChannelId(), video.getId()), uploadVideo, setting);

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
    }
}
