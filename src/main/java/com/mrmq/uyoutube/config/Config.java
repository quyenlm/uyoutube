package com.mrmq.uyoutube.config;


import com.mrmq.uyoutube.YouTubeService;
import com.mrmq.uyoutube.beans.ScreenSetting;
import com.mrmq.uyoutube.helper.FileHelper;
import org.springframework.context.annotation.*;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    public static Map<String, ScreenSetting> screenSetting = new ConcurrentHashMap<String, ScreenSetting>();
    public static Map<String, String> messages = new ConcurrentHashMap<String, String>();

    private static final String youtubeWatchUrl = "https://www.youtube.com/watch?v=";
    private static final String appName = "uYouTube";
    private static String apiKey = "AIzaSyB46r92ADOWDL3CIFdcMaB8auZ3_2iEmK4";
    private static String homePath = ".";
    private static String downloadPath = "./download/videos/";

    static {
        screenSetting.put(ScreenSetting.SCREEN_MAIN, new ScreenSetting(1024, 768));
        screenSetting.put(ScreenSetting.SCREEN_LOGIN, new ScreenSetting(300, 275));
    }

    public static void init(){
        try {
            File homeDir = new File(homePath);
            if(!homeDir.exists())
                homeDir.mkdirs();

            File downloadDir = new File(downloadPath);
            if(!downloadDir.exists())
                downloadDir.mkdirs();

            File channelDir = new File(FileHelper.makerChannelDataPath());
            if(!channelDir.exists())
                channelDir.mkdirs();
        } catch (Exception e) {

        }
    }

    public static Map<String, ScreenSetting> getScreenSetting() {
        return screenSetting;
    }

    public static String getAppName() {
        return appName;
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getDownloadPath() {
        return downloadPath;
    }

    public static void setDownloadPath(String downloadPath) {
        Config.downloadPath = downloadPath;
    }

    public static String getHomePath() {
        return homePath;
    }

    public static void setHomePath(String homePath) {
        Config.homePath = homePath;
    }

    public static String getYoutubeWatchUrl() {
        return youtubeWatchUrl;
    }

    public static String getVideoType() {
        return ".mp4";
    }
}