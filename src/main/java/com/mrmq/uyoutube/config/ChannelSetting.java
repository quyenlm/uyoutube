package com.mrmq.uyoutube.config;

import com.mrmq.uyoutube.helper.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ChannelSetting {
    private static final Logger logger = LoggerFactory.getLogger(ChannelSetting.class);

    private String channelId;
    private String oldTitleReplace;
    private String newTitleReplace;
    private String oldDescReplace;
    private String newDescReplace;
    private String descAppend;
    private List<String> defaultTags;
    private String privacyStatus = "private";

    public static ChannelSetting load(String channelId) {
        Properties prop = new Properties();
        InputStream input = null;
        ChannelSetting setting = new ChannelSetting();
        try {
            input = new FileInputStream(FileHelper.makerChannelDataPath() + File.separator + channelId + ".properties");

            // load a properties file
            prop.load(input);

            // set the properties value
            setting.setChannelId(prop.getProperty("channelId"));
            setting.setOldTitleReplace(prop.getProperty("oldTitleReplace"));
            setting.setNewTitleReplace(prop.getProperty("newTitleReplace"));
            setting.setOldDescReplace(prop.getProperty("oldDescReplace"));
            setting.setNewDescReplace(prop.getProperty("newDescReplace"));
            setting.setDescAppend(prop.getProperty("descAppend"));
            setting.setPrivacyStatus(prop.getProperty("privacyStatus"));
            setting.setDefaultTags(FileHelper.toTags(prop.getProperty("defaultTags")));
        } catch (IOException io) {
            logger.error(io.getMessage(), io);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }
        return setting;
    }

    public static void save(ChannelSetting setting) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            output = new FileOutputStream(FileHelper.makerChannelDataPath() + File.separator + setting.getChannelId() + ".properties");

            // set the properties value
            prop.setProperty("channelId", setting.getChannelId());
            prop.setProperty("oldTitleReplace", setting.getOldTitleReplace());
            prop.setProperty("newTitleReplace", setting.getNewTitleReplace());
            prop.setProperty("oldDescReplace", setting.getOldDescReplace());
            prop.setProperty("newDescReplace", setting.getNewDescReplace());
            prop.setProperty("descAppend", setting.getDescAppend());
            prop.setProperty("privacyStatus", setting.getPrivacyStatus());
            prop.setProperty("defaultTags", FileHelper.toTags(setting.getDefaultTags()));

            // save properties to project root folder
            prop.store(output, "Settings for channel: " + setting.getChannelId());
        } catch (IOException io) {
            logger.error(io.getMessage(), io);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
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

    public String getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(String privacyStatus) {
        this.privacyStatus = privacyStatus;
    }

    @Override
    public String toString() {
        return "ChannelSetting{" +
                "channelId='" + channelId + '\'' +
                ", oldTitleReplace='" + oldTitleReplace + '\'' +
                ", newTitleReplace='" + newTitleReplace + '\'' +
                ", oldDescReplace='" + oldDescReplace + '\'' +
                ", newDescReplace='" + newDescReplace + '\'' +
                ", descAppend='" + descAppend + '\'' +
                ", defaultTags=" + defaultTags +
                ", privacyStatus='" + privacyStatus + '\'' +
                '}';
    }
}