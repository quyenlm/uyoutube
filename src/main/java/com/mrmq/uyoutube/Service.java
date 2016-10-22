package com.mrmq.uyoutube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.DownloadService;
import com.mrmq.uyoutube.config.Config;
import com.mrmq.uyoutube.data.UploadVideo;
import com.mrmq.uyoutube.helper.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Service extends Thread {
    protected static final Logger logger = LoggerFactory.getLogger(DownloadService.class);
    protected boolean isRunning = false;
    protected YouTube youtube;
    protected YouTubeService youTubeService;
    protected BlockingQueue<Video> queues = new LinkedBlockingQueue<Video>();

    public Service(YouTube youtube) {
        this.youtube = youtube;
    }

    @Override
    public void run() {
        isRunning = true;
    }

    public void add(Video video) {
        queues.add(video);
    }

    public void stopService() {
        isRunning  = false;
        this.interrupt();
    }

    public YouTubeService getYouTubeService() {
        return youTubeService;
    }

    public void setYouTubeService(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }
}
