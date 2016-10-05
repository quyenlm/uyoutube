package com.mrmq.uyoutube;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mrmq.uyoutube.helper.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;


public class YouTubeService {
    private static final Logger logger = LoggerFactory.getLogger(YouTubeService.class);

    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private YouTube youtube;
    private Credential credential;
    private YouTubeService service;
    private String ownerEmail;

    private Map<String, Video> videos = com.google.common.collect.Maps.newConcurrentMap();
    private Channel channel;

    public YouTubeService(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public void loadConfig() throws IOException {
        BufferedReader reader = null;
        try {
            File configFile = new File(FileHelper.makerChannelFileName(ownerEmail));
            if(!configFile.exists() || configFile.length() == 0)
                init(configFile);

            //read data from csv
            //line 1: channel_email, channel_id, channel_name, tags
            //line 2: source_id, video_id, video_title, video_desc, video_tags

            String line;
            String[] arrTmp;
            reader = new BufferedReader(new FileReader(configFile));
            int count = 0;
            while ((line = reader.readLine()) != null) {
                count++;
                arrTmp = line.split(",", -1);

                if(count == 2) {
                    channel = new Channel();
                    channel.setId(arrTmp[1]); //Id
                } else if (count > 2) {
                    Video video = new Video();
                    video.setId(arrTmp[1]); //source_id

                    VideoSnippet snippet = new VideoSnippet();

                    snippet.setTitle(arrTmp[2]);
                    snippet.setDescription(arrTmp[3]);

                    if(!Strings.isNullOrEmpty(arrTmp[4])) {
                        String[] arrTags = arrTmp[4].split("<>");
                        snippet.setTags(Lists.newArrayList(arrTags));
                    }

                    videos.put(arrTmp[0], video);
                }
            }
            logger.info("Loaded channel: " + channel);
            logger.info("Channel videos: " + videos);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(reader != null)
                reader.close();
        }
    }

    private void init(File configFile) throws IOException {
        BufferedWriter writer = null;
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            writer = new BufferedWriter(new FileWriter(configFile));

            //write data from csv
            //line 1, 2: email, channel_id, channel_name, tags
            writer.write("#channel_email, channel_id, channel_name, channel_tags"); writer.newLine();
            writer.write(ownerEmail + FileHelper.CSV_SPLIT + FileHelper.CSV_SPLIT + FileHelper.CSV_SPLIT); writer.newLine();
            //line 3, 4: source_id, video_id, video_title, video_desc, video_tags
            writer.write("#source_id, video_id, video_title, video_desc, video_tags"); writer.newLine();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(writer != null)
                writer.close();
        }
    }

    private Credential login() throws IOException {
        if(credential == null) {
            // This OAuth 2.0 access scope allows for full read/write access to the
            // authenticated user's account.
            List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

            // Authorize the request.
            credential = Auth.authorize(scopes, "updatevideo");


        }

        return credential;
    }

    private String[] tags = new String[] {};
    private YouTube getYouTube() throws IOException {
        if(youtube == null) {
            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, login())
                    .setApplicationName("uYouTube").build();
        }
        return youtube;
    }

    private void uploadVideo(String videoDirectory) throws IOException {

    }
}