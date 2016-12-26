package com.mrmq.uyoutube.config;


import com.google.common.collect.Lists;
import com.mrmq.uyoutube.beans.ScreenSetting;
import com.mrmq.uyoutube.helper.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    public static Map<String, ScreenSetting> screenSetting = new ConcurrentHashMap<String, ScreenSetting>();
    public static Map<String, String> messages = new ConcurrentHashMap<String, String>();

    private String youtubeWatchUrl = "https://www.youtube.com/watch?v=";
    private String appName = "uYouTube";
    private String apiKey = "AIzaSyB46r92ADOWDL3CIFdcMaB8auZ3_2iEmK4";
    private String homePath = ".";
    private String downloadPath = "./download/videos/";
    private String toolPath = "./bin/";
    private String mergeMp4WebmTemplate;

    private Map<String, ChannelSetting> channelSettings = new ConcurrentHashMap<String, ChannelSetting>();

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

    public void init() throws FileNotFoundException {
        File homeDir = new File(homePath);
        if(!homeDir.exists())
            homeDir.mkdirs();

        File downloadDir = new File(downloadPath);
        if(!downloadDir.exists())
            downloadDir.mkdirs();

        File channelDir = new File(FileHelper.makerChannelDataPath());
        if(!channelDir.exists())
            channelDir.mkdirs();
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

    public ChannelSetting getChannelSettings(String channelId) {
        return channelSettings.get(channelId);
    }
    public ChannelSetting setChannelSettings(ChannelSetting setting) {
        return channelSettings.put(setting.getChannelId(), setting);
    }

    public String getToolPath() {
        return toolPath;
    }

    public void setToolPath(String toolPath) {
        this.toolPath = toolPath;
    }

    public String getFFMPEGPath() {
        return getToolPath() + "ffmpeg";
    }

    public String getMergeMp4WebmTemplate() throws FileNotFoundException {
        if(mergeMp4WebmTemplate == null) {
            mergeMp4WebmTemplate = FileHelper.readStream(new FileInputStream(FileHelper.getMergeMp4WebmTemplate()));
            mergeMp4WebmTemplate = mergeMp4WebmTemplate.replaceAll("path_to_ffmpeg", getToolPath());
        }
        return mergeMp4WebmTemplate;
    }
}