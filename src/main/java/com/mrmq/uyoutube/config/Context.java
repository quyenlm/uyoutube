package com.mrmq.uyoutube.config;


import com.mrmq.uyoutube.DownloadService;
import com.mrmq.uyoutube.YouTubeService;

public class Context {
    private static YouTubeService youTubeService;
    private static DownloadService downloadService;

    public YouTubeService getYouTubeService() {
        return youTubeService;
    }

    public static void setYouTubeService(YouTubeService youTubeService) {
        youTubeService = youTubeService;
    }

    public static DownloadService getDownloadService() {
        return downloadService;
    }

    public static void setDownloadService(DownloadService downloadService) {
        downloadService = downloadService;
    }
}
