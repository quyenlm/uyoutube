package com.mrmq.uyoutube.helper;

import com.google.api.services.youtube.model.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {
    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);
    public static final String CSV_SPLIT = "<>";
    public static final String TAG_SPLIT = "#";

    public static String makerChannelFileName(String channelEmail) {
        return channelEmail + ".ini";
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

        return strB.toString();
    }
}