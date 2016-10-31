package com.mrmq.uyoutube.helper;

import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.google.api.services.youtube.model.Video;
import com.mrmq.uyoutube.config.Config;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileHelper {
    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);
    public static final String CSV_SPLIT = "<>";
    public static final String TAG_SPLIT = "><";
    public static final String VIDEO_SUFFIX = "_Merged";

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
            return dirPath + videoId + VIDEO_SUFFIX + ".mp4";
        else return dirPath + File.separator + videoId + VIDEO_SUFFIX + ".mp4";
    }

    public static String createVideoFile(String dirPath, String channelId, String fileName) {
        if(dirPath.endsWith(File.separator))
            return dirPath + channelId + File.separator + fileName + Config.getInstance().getVideoType();
        else return dirPath + File.separator + channelId + File.separator + fileName + Config.getInstance().getVideoType();
    }

    public static String getFilePrefix(String fileName){
        for(String postfix : Config.getInstance().getVideoType()) {
            if(fileName.contains(VIDEO_SUFFIX))
                return fileName.replaceAll(VIDEO_SUFFIX, "").substring(0, fileName.lastIndexOf(VIDEO_SUFFIX));
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

    public static Video makeUploadVideo(Video origin) {
        Video video = new Video();
        BeanUtils.copyProperties(origin, video);

        Preconditions.checkNotNull(Config.getInstance().getOldTitleReplace(), "OldTitleReplace can not be null");
        Preconditions.checkNotNull(Config.getInstance().getNewTitleReplace(), "NewTitleReplace can not be null");
        Preconditions.checkNotNull(Config.getInstance().getOldDescReplace(), "OldDescReplace can not be null");
        Preconditions.checkNotNull(Config.getInstance().getNewDescReplace(), "NewDescReplace can not be null");

        String title;
        if(StringUtils.isNoneEmpty(Config.getInstance().getOldTitleReplace()) && origin.getSnippet().containsKey(Config.getInstance().getOldTitleReplace()))
            title = origin.getSnippet().getTitle().replace(Config.getInstance().getOldTitleReplace(), Config.getInstance().getNewTitleReplace());
        else if(StringUtils.isNoneEmpty(Config.getInstance().getNewTitleReplace()))
            title = Config.getInstance().getNewTitleReplace() + " " + origin.getSnippet().getTitle();
        else
            title = origin.getSnippet().getTitle();
        video.getSnippet().setTitle(title);

        //Description

        String desc;
        if(StringUtils.isNoneEmpty(Config.getInstance().getOldDescReplace()) && origin.getSnippet().getDescription().contains(Config.getInstance().getOldDescReplace()))
            desc = origin.getSnippet().getTitle().replace(Config.getInstance().getOldDescReplace(), Config.getInstance().getNewDescReplace());
        else if(StringUtils.isNoneEmpty(Config.getInstance().getNewDescReplace()))
            desc = Config.getInstance().getNewDescReplace() + " " + origin.getSnippet().getDescription();
        else
            desc = origin.getSnippet().getDescription();
        if(StringUtils.isNoneEmpty(Config.getInstance().getDescAppend()))
            desc += Config.getInstance().getDescAppend();
        video.getSnippet().setDescription(desc);

        return video;
    }

    public static void mergedFile(String videoPath) {
        File files = new File(videoPath);

        Map<String, String> cache = new HashMap<String, String>();
        Map<String, String> cache2 = new HashMap<String, String>();

        for(File file : files.listFiles()) {
            if(file.getName().endsWith(".mp4"))
                cache.put(file.getName().replace(".mp4", ""), "");
        }

        for(File file : files.listFiles()) {
            if(file.getName().endsWith(".webm")) {
                cache2.put(file.getName().replace(".webm", ""), "");
            }
        }

        for(String mp4 : cache.keySet()) {
            if(cache2.get(mp4) != null)
                System.out.println(String.format("ffmpeg -i %s.mp4 -i %s.webm -c:a aac -c:v libx264 -strict -2 -c:s copy %s%s.mp4", mp4, mp4, mp4, VIDEO_SUFFIX));
        }
    }
}