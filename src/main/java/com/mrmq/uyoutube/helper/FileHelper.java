package com.mrmq.uyoutube.helper;

import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class FileHelper {
    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);
    public static final String CSV_SPLIT = "<>";
    public static final String TAG_SPLIT = "><";
    public static final String VIDEO_SUFFIX = "_merged";

    public static String makerChannelDataPath() {
        return Config.getInstance().getHomePath() + "data";
    }

    public static String makerChannelFileName(String channelEmail) {
        return Config.getInstance().getHomePath() + "data" + File.separator +  channelEmail + ".ini";
    }

    public static String createFilePath(String dirPath, String fileName) {
        if(dirPath.endsWith(File.separator))
            return dirPath + fileName;
        else return dirPath + File.separator + fileName;
    }

    public static String createVideoFile(String dirPath, String fileName) {
        if(dirPath.endsWith(File.separator))
            return dirPath + fileName;
        else return dirPath + File.separator + fileName;
    }

    public static String createVideoUploadFile(String dirPath, String videoId) {
        if(dirPath.endsWith(File.separator))
            return dirPath + videoId + VIDEO_SUFFIX + Config.getInstance().getVideoType();
        else return dirPath + File.separator + videoId + Config.getInstance().getVideoType();
    }

    public static String createVideoFile(String dirPath, String channelId, String fileName) {
        if(dirPath.endsWith(File.separator))
            return dirPath + channelId + File.separator + fileName + Config.getInstance().getVideoType();
        else return dirPath + File.separator + channelId + File.separator + fileName + Config.getInstance().getVideoType();
    }

    public static String getFilePrefix(String fileName){
        for(String postfix : Config.getInstance().getVideoType()) {
            if(fileName.contains(VIDEO_SUFFIX))
                return fileName.replaceAll(VIDEO_SUFFIX, "").substring(0, fileName.lastIndexOf("."));
            if (fileName.endsWith(postfix))
                return fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    public static String toCsv(Video video) {
        StringBuilder strB = new StringBuilder();

        //#video_id, channel_id, video_title, video_desc, video_tags
        strB.append(video.getId());
        strB.append(CSV_SPLIT);
        strB.append(video.getSnippet().getChannelId());
        strB.append(CSV_SPLIT);
        strB.append(video.getSnippet().getTitle());
        strB.append(CSV_SPLIT);
        strB.append(video.getSnippet().getDescription());
        strB.append(CSV_SPLIT);
        strB.append(toTags(video.getSnippet().getTags()));
        return strB.toString();
    }

    public static String toCsv(String sourceVideo, Video video) {
        StringBuilder strB = new StringBuilder();

        //#source_id, video_id, video_title, video_desc, video_tags
        strB.append(sourceVideo);
        strB.append(CSV_SPLIT);
        strB.append(video.getId());
        strB.append(CSV_SPLIT);
        strB.append(video.getSnippet().getTitle());
        strB.append(CSV_SPLIT);
        strB.append(video.getSnippet().getDescription());
        strB.append(CSV_SPLIT);
        strB.append(toTags(video.getSnippet().getTags()));
        return strB.toString();
    }

    public static String toTags(List<String> items) {
        if(items == null)
            return "";

        StringBuilder strB = new StringBuilder();
        boolean isFirst = true;
        for (String item: items) {
            if(isFirst)
                isFirst = false;
            else
                strB.append(TAG_SPLIT);

            strB.append(item);
        }

        return strB.toString();
    }
}