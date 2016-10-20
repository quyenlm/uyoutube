package com.mrmq.uyoutube.config;


import com.mrmq.uyoutube.YouTubeService;
import com.mrmq.uyoutube.beans.ScreenSetting;
import org.springframework.context.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    public static Map<String, ScreenSetting> screenSetting = new ConcurrentHashMap<String, ScreenSetting>();
    public static Map<String, String> messages = new ConcurrentHashMap<String, String>();

    public static final String YOUTUBE__WATCH_URL = "https://www.youtube.com/watch?v=";
//    public static YouTubeService uouTubeService;

    private static final String appName = "uYouTube";
    private static String apiKey = "AIzaSyB46r92ADOWDL3CIFdcMaB8auZ3_2iEmK4";
    private static String homePath = "";
    private static String downloadPath = "G:\\VIDEOS\\";

    static {
        screenSetting.put(ScreenSetting.SCREEN_MAIN, new ScreenSetting(1024, 768));
        screenSetting.put(ScreenSetting.SCREEN_LOGIN, new ScreenSetting(300, 275));
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

    public static String getVideoType() {
        return ".mp4";
    }
}