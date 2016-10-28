package com.mrmq.uyoutube.beans;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mrmq.uyoutube.config.Config;
import com.mrmq.uyoutube.helper.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VideoDirectory extends File {
    private static final Logger logger = LoggerFactory.getLogger(VideoDirectory.class);
    public static final String VIDEOS_DIR_INI = "videos.ini";

    private String dirPath;
    private Map<String, Video> videos = com.google.common.collect.Maps.newConcurrentMap();

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

        for (File file: this.listFiles()) {
        }
        return true;
    }

    public Map<String, Video> loadInfo() throws IOException {
        logger.info("loadInfo dirPath: {}", dirPath);
        BufferedReader reader = null;
        videos.clear();

        try {
            File dir = new File(dirPath);
            if(!dir.exists())
                dir.mkdirs();

            File configFile = new File(FileHelper.createFilePath(dirPath, VIDEOS_DIR_INI));
            if(!configFile.exists() || configFile.length() == 0)
                init(configFile);

            //read data from csv
            //line 1: #video_id, channel_id, video_title, video_desc, video_tags

            String line;
            String[] arrTmp;
            reader = new BufferedReader(new FileReader(configFile));
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                arrTmp = line.split(FileHelper.CSV_SPLIT, -1);

                if(isFirst) {
                    isFirst = false;
                } else {
                    Video video = new Video();
                    video.setId(arrTmp[0]); //video_id

                    VideoSnippet snippet = new VideoSnippet();
                    snippet.setChannelId(arrTmp[1]);
                    snippet.setTitle(arrTmp[2]);
                    snippet.setDescription(arrTmp[3]);

                    if(!Strings.isNullOrEmpty(arrTmp[4])) {
                        String[] arrTags = arrTmp[4].split(FileHelper.TAG_SPLIT);
                        snippet.setTags(Lists.newArrayList(arrTags));
                    }
                    video.setSnippet(snippet);

                    videos.put(video.getId(), video);
                }
            }

            filterRemovedVideos();
            logger.info("Total videos: " + videos.size());
            logger.info("Directory videos: " + videos);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(reader != null)
                reader.close();
        }

        return videos;
    }

    private void filterRemovedVideos() {
        Map<String, File> currentFiles = new ConcurrentHashMap<String, File>();

        File[] listFile = listFiles();
        if(listFile == null || listFile.length == 0)
            return;

        for(File file : listFiles())
            currentFiles.put(FileHelper.getFilePrefix(file.getName()), file);

        Iterator<String> itKey = videos.keySet().iterator();
        while (itKey.hasNext()) {
            String fileName = itKey.next();
            if (!currentFiles.containsKey(fileName))
                itKey.remove();
        }
    }

    private void init(File configFile) throws IOException {
        BufferedWriter writer = null;
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            writer = new BufferedWriter(new FileWriter(configFile));

            //write data to csv
            //line 1, 2: video_id, channel_id, video_title, video_desc, video_tags
            writer.write("#video_id<>channel_id<>video_title<>video_desc<>video_tags"); writer.newLine();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(writer != null)
                writer.close();
        }
    }

    public synchronized boolean addVideo(Video video) throws IOException {
        BufferedWriter writer = null;
        boolean result = false;
        try {
            if(videos.containsKey(video.getId()))
                return false;

            writer = new BufferedWriter(new FileWriter(FileHelper.createFilePath(dirPath, VIDEOS_DIR_INI), true));
            //write data to csv
            //line 1, 2: video_id, channel_id, video_title, video_desc, video_tags
            writer.write(FileHelper.toCsv(video)); writer.newLine();

            videos.put(video.getId(), video);
            result = true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(writer != null)
                writer.close();
        }

        return result;
    }
}
