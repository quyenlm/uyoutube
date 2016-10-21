package com.mrmq.uyoutube;


import com.mrmq.uyoutube.beans.VideoDirectory;
import com.mrmq.uyoutube.config.Config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Context {
    private static YouTubeService youTubeService;
    private static DownloadService downloadService;

    private static ConcurrentMap<String, Object> beans = new ConcurrentHashMap<String, Object>();


    public static YouTubeService getYouTubeService() {
        return youTubeService;
    }

    public static void setYouTubeService(YouTubeService youTubeService) {
        youTubeService = youTubeService;
    }

    public static DownloadService getDownloadService() {
        return downloadService;
    }

    public static void setDownloadService(DownloadService downloadService) {
        Context.downloadService = downloadService;
    }

    public static <T> T getBeans(String key) {
        return (T) beans.get(key);
    }

    public static <T> T setBeans(String key, Object bean) {
        return (T) beans.put(key, bean);
    }

    public static VideoDirectory getVideosDir(String folderName) {
        VideoDirectory channelDir = Context.getBeans(folderName);
        if(channelDir == null)
            channelDir = setBeans(folderName, new VideoDirectory(Config.getDownloadPath() + folderName));
        return channelDir;
    }
}
