package com.mrmq.uyoutube;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mrmq.uyoutube.authenticate.Auth;
import com.mrmq.uyoutube.data.MyUploads;
import com.mrmq.uyoutube.helper.FileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Iterator;
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
    private String channelEmail;
    private Channel channel;
    private Map<String, Video> videos = com.google.common.collect.Maps.newConcurrentMap();

    public YouTubeService() {

    }

    public YouTubeService(String ownerEmail) {
        this.channelEmail = ownerEmail;
    }

    private void loadConfig() throws IOException {
        BufferedReader reader = null;
        try {
            File configFile = new File(FileHelper.makerChannelFileName(channelEmail));
            if(!configFile.exists() || configFile.length() == 0)
                initConfig(configFile);

            //read data from csv
            //line 1: channel_email, channel_id, channel_name, channel_desc
            //line 2: source_id, video_id, video_title, video_desc, video_tags

            String line;
            String[] arrTmp;
            reader = new BufferedReader(new FileReader(configFile));
            int count = 0;
            while ((line = reader.readLine()) != null) {
                count++;
                arrTmp = line.split(FileHelper.CSV_SPLIT, -1);

                if(count == 2) {
                    channel = new Channel();
                    channel.setId(arrTmp[1]); //Id

                    ChannelSnippet channelSnip = new ChannelSnippet();
                    channelSnip.setTitle(arrTmp[2]);
                    channelSnip.setDescription(arrTmp[3]);
                    channel.setSnippet(channelSnip);
                } else if (count > 3) {
                    Video video = new Video();
                    video.setId(arrTmp[1]); //source_id

                    VideoSnippet snippet = new VideoSnippet();
                    snippet.setChannelId(channel.getId());
                    snippet.setTitle(arrTmp[2]);
                    snippet.setDescription(arrTmp[3]);

                    if(!Strings.isNullOrEmpty(arrTmp[4])) {
                        String[] arrTags = arrTmp[4].split(FileHelper.TAG_SPLIT);
                        snippet.setTags(Lists.newArrayList(arrTags));
                    }

                    videos.put(arrTmp[0], video);
                }
            }
            logger.info("Config channel: " + channel);
            logger.info("Config videos size: " + videos.size());
            logger.debug("Config videos: " + videos);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(reader != null)
                reader.close();
        }
    }

    private void initConfig(File configFile) throws IOException {
        BufferedWriter writer = null;
        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            writer = new BufferedWriter(new FileWriter(configFile));

            //write data from csv
            //line 1, 2: channel_email, channel_id, channel_name, channel_desc
            writer.write("#channel_email<>channel_id<>channel_name<>channel_desc"); writer.newLine();
            writer.write(channelEmail + FileHelper.CSV_SPLIT + FileHelper.CSV_SPLIT + FileHelper.CSV_SPLIT); writer.newLine();
            //line 3, 4: source_id, video_id, video_title, video_desc, video_tags
            writer.write("#source_id<>video_id<>video_title<>video_desc<>video_tags"); writer.newLine();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(writer != null)
                writer.close();
        }
    }

    private void saveConfig(Map<String, Video> videos) throws IOException {
        BufferedWriter writer = null;
        try {
            //create a temp file
            File temp = File.createTempFile("temp-file-name", ".tmp");
            initConfig(temp);
            writer = new BufferedWriter(new FileWriter(temp));

            //write data to csv
            //source_id, video_id, video_title, video_desc, video_tags
            Iterator<String> iter = videos.keySet().iterator();
            while(iter.hasNext()) {
                String sourceId = iter.next();
                Video video = videos.get(sourceId);
                writer.write(FileHelper.toCsv(sourceId, video));
                writer.newLine();
            }

            //Rename temp to configFile
            File configFile = new File(FileHelper.makerChannelFileName(channelEmail));
            if(!configFile.exists())
                configFile.createNewFile();
            temp.renameTo(configFile);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if(writer != null)
                writer.close();
        }


    }

    public boolean login() throws IOException {
        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

        // Authorize the request.
        Credential credential = login(scopes, "updatevideo");
        return credential != null;
    }

    private Credential login(List<String> scopes, String credentialDatastore) throws IOException {
        if(credential == null) {
            // Authorize the request.
            credential = Auth.authorize(scopes, credentialDatastore);

            loadConfig();
        }

        return credential;
    }

    private YouTube getYouTube() throws IOException {
        if(youtube == null) {
            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("uYouTube").build();
        }
        return youtube;
    }

    private void uploadVideo(String videoDirectory) throws IOException {

    }

    public Map<String, Video> getMyUpload() throws IOException {
        Map<String, Video> channelVideos = com.google.common.collect.Maps.newConcurrentMap();
        List<Channel> lstChannel = MyUploads.getMyChannels(getYouTube());
        if(lstChannel != null)
            for (Channel channel : MyUploads.getMyChannels(getYouTube())) {
                List<Video> lstVideo = MyUploads.getChannelVideos(getYouTube(), channel);

                Iterator<Video> iter = lstVideo.iterator();
                while(iter.hasNext()) {
                    Video video = iter.next();
                    channelVideos.put(video.getId(), video);
                }
            }

        boolean needResave = channelVideos.size() < videos.size();
        //filter no longer exist video from config file
//        Iterator<Video> iter = videos.values().iterator();
//        while(iter.hasNext()) {
//            Video video = iter.next();
//            if(!channelVideos.containsKey(video.getId())) {
////                logger.info("Remove videoId: {}, videoTitle: {}", video.getId(), video.getSnippet().getTitle());
////                iter.remove();
////                needResave = true;
//            }
//        }

        videos = channelVideos;
        //Resave config File
        if(needResave) {
            saveConfig(videos);
        }

        logger.info("Loaded channel: " + channel);
        logger.info("Total videos: " + channelVideos.size());
        logger.debug("Channel videos: " + channelVideos);
        return videos;
    }

    public String getChannelEmail() {
        return channelEmail;
    }

    public void setChannelEmail(String channelEmail) {
        this.channelEmail = channelEmail;
    }
}