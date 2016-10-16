package com.mrmq.uyoutube.config;


import com.mrmq.uyoutube.YouTubeService;
import com.mrmq.uyoutube.beans.ScreenSetting;
import org.springframework.context.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    public static Map<String, ScreenSetting> screenSetting = new ConcurrentHashMap<String, ScreenSetting>();

    public static final String YOUTUBE__WATCH_URL = "https://www.youtube.com/watch?v=";
    public static YouTubeService uouTubeService;

    static {
        screenSetting.put(ScreenSetting.SCREEN_MAIN, new ScreenSetting(512, 512));
        screenSetting.put(ScreenSetting.SCREEN_LOGIN, new ScreenSetting(300, 275));
    }

    public static Map<String, ScreenSetting> getScreenSetting() {
        return screenSetting;
    }
}