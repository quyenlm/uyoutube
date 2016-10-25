package com.mrmq.uyoutube;


import com.mrmq.uyoutube.beans.VideoDirectory;
import com.mrmq.uyoutube.config.Config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Context {
    private static YouTubeService youTubeService;
    private static DownloadService downloadService;
    private static UploadService uploadService;

    private static ConcurrentMap<String, Object> beans = new ConcurrentHashMap<String, Object>();
    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static YouTubeService getYouTubeService() {
        return youTubeService;
    }

    public static void setYouTubeService(YouTubeService youTubeService) {
        Context.youTubeService = youTubeService;
    }

    public static DownloadService getDownloadService() {
        return downloadService;
    }

    public static void setDownloadService(DownloadService downloadService) {
        Context.downloadService = downloadService;
    }

    public static UploadService getUploadService() {
        return uploadService;
    }

    public static void setUploadService(UploadService uploadService) {
        Context.uploadService = uploadService;
    }

    public static <T> T getBeans(String key) {
        return (T) beans.get(key);
    }

    public static <T> T setBeans(String key, T bean) {
        beans.put(key, bean);
        return bean;
    }

    public static VideoDirectory getVideosDir(String folderName) {
        VideoDirectory channelDir = Context.getBeans(folderName);
        if(channelDir == null)
            channelDir = setBeans(folderName, new VideoDirectory(Config.getInstance().getDownloadPath() + folderName));
        return channelDir;
    }

    public static ExecutorService getExecutor() {
        return executor;
    }
}
