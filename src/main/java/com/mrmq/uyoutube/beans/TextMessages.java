package com.mrmq.uyoutube.beans;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TextMessages {
    public static final String SCREEN_MAIN_TITLE = "uYou";
    public static final String SCREEN_LOGIN_TITLE = "Login";
    public static final String SCREEN_REUP = "Reup";

    public static Map<String, String> screenSetting = new ConcurrentHashMap<String, String>();

    static {
        screenSetting.put(SCREEN_MAIN_TITLE, "uYou");
        screenSetting.put(SCREEN_LOGIN_TITLE, "Login");
        screenSetting.put(SCREEN_REUP, "Reup");
    }

    public static String getText(String key) {
        return screenSetting.get(key);
    }
}
