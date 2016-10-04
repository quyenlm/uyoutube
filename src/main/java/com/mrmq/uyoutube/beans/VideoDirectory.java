package com.mrmq.uyoutube.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class VideoDirectory extends File {
    private static final Logger logger = LoggerFactory.getLogger(VideoDirectory.class;

    private String dirPath;

    public VideoDirectory(String dirPath){
        super(dirPath);
        this.dirPath = dirPath;
    }

    private boolean validate() {
        if(!this.exists()) {
            logger.error(String.format("NOT exit directory: '%s'"), dirPath);
            return false;
        }

        if(!this.isDirectory()) {
            logger.error(String.format("NOT is directory: '%s'"), dirPath);
            return false;
        }

        for (File file: dir.listFiles()) {
        }
    }

}
