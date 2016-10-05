package com.mrmq.uyoutube.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper {
    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);
    public static final String CSV_SPLIT = ",";

    public static String makerChannelFileName(String channelEmail) {
        return channelEmail + ".ini";
    }
}
