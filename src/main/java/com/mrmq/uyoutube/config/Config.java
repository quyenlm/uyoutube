package com.mrmq.uyoutube.config;


import com.google.common.collect.Lists;
import com.mrmq.uyoutube.beans.ScreenSetting;
import com.mrmq.uyoutube.helper.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    public static Map<String, ScreenSetting> screenSetting = new ConcurrentHashMap<String, ScreenSetting>();
    public static Map<String, String> messages = new ConcurrentHashMap<String, String>();

    private String youtubeWatchUrl = "https://www.youtube.com/watch?v=";
    private String appName = "uYouTube";
    private String apiKey = "AIzaSyB46r92ADOWDL3CIFdcMaB8auZ3_2iEmK4";
    private String homePath = ".";
    private String downloadPath = "./download/videos/";

    private String oldTitleReplace;
    private String newTitleReplace;
    private String oldDescReplace;
    private String newDescReplace;
    private String descAppend;
    private List<String> defaultTags = Lists.asList("Larva TUBA", new String[]{"Larva ","TUBA","funny larva","cartoon","lovely cartoon","funny cartoon","best cartoon","best funny"});
    private static Config instance;

    static {
        screenSetting.put(ScreenSetting.SCREEN_MAIN, new ScreenSetting(1024, 768));
        screenSetting.put(ScreenSetting.SCREEN_LOGIN, new ScreenSetting(300, 275));
    }

    public static void setInstance(Config config) {
        Config.instance = config;
    }

    public static Config getInstance() {
        return Config.instance;
    }

    public void init(){
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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public String getHomePath() {
        return homePath;
    }

    public void setHomePath(String homePath) {
        this.homePath = homePath;
    }

    public List<String> getVideoType() {
        return Lists.asList(".mp4", new String[]{".webm"});
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setYoutubeWatchUrl(String youtubeWatchUrl) {
        this.youtubeWatchUrl = youtubeWatchUrl;
    }

    public String getYoutubeWatchUrl() {
        return youtubeWatchUrl;
    }

    public String getOldTitleReplace() {
        return oldTitleReplace;
    }

    public void setOldTitleReplace(String oldTitleReplace) {
        this.oldTitleReplace = oldTitleReplace;
    }

    public String getNewTitleReplace() {
        return newTitleReplace;
    }

    public void setNewTitleReplace(String newTitleReplace) {
        this.newTitleReplace = newTitleReplace;
    }

    public String getOldDescReplace() {
        return oldDescReplace;
    }

    public void setOldDescReplace(String oldDescReplace) {
        this.oldDescReplace = oldDescReplace;
    }

    public String getNewDescReplace() {
        return newDescReplace;
    }

    public void setNewDescReplace(String newDescReplace) {
        this.newDescReplace = newDescReplace;
    }

    public String getDescAppend() {
        return descAppend;
    }

    public void setDescAppend(String descAppend) {
        this.descAppend = descAppend;
    }

    public List<String> getDefaultTags() {
        return defaultTags;
    }

    public void setDefaultTags(List<String> defaultTags) {
        this.defaultTags = defaultTags;
    }
}