package com.mrmq.uyoutube;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mrmq.uyoutube.authenticate.Auth;
import com.mrmq.uyoutube.beans.ErrorCode;
import com.mrmq.uyoutube.beans.Result;
import com.mrmq.uyoutube.beans.VideoDirectory;
import com.mrmq.uyoutube.config.ChannelSetting;
import com.mrmq.uyoutube.config.Config;
import com.mrmq.uyoutube.data.MyUploads;
import com.mrmq.uyoutube.data.UpdateVideo;
import com.mrmq.uyoutube.data.VideoSearch;
import com.mrmq.uyoutube.helper.FileHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class YouTubeService {
    private static final Logger logger = LoggerFactory.getLogger(YouTubeService.class);

    private YouTube youtube;
    private Credential credential;
    private YouTubeService service;
    private String channelEmail;
    private Channel channel;
    private String infoPath;
    private Map<String, Video> videos = com.google.common.collect.Maps.newConcurrentMap();

    public YouTubeService() {
    }

    public YouTubeService(String ownerEmail) {
        this.channelEmail = ownerEmail;
        this.infoPath = FileHelper.makerChannelFileName(channelEmail);
    }

    private void loadConfig() throws IOException {
        BufferedReader reader = null;
        try {
            File configFile = new File(infoPath);
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
            if(!configFile.getParentFile().exists())
                configFile.getParentFile().mkdirs();

            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            List<Channel> channels = MyUploads.getMyChannels(getYouTube());
            if(channels != null && channels.size() > 0)
                channel = channels.get(0);

            writer = new BufferedWriter(new FileWriter(configFile));

            //write data from csv
            //line 1, 2: channel_email, channel_id, channel_name, channel_desc
            writer.write("#channel_email<>channel_id<>channel_name<>channel_desc"); writer.newLine();
            if(channel != null) {
                String channelTitle = channel.getSnippet() != null ? channel.getSnippet().getTitle() : "";
                String channelDesc = channel.getSnippet() != null ? channel.getSnippet().getDescription() : "";

                writer.write(channelEmail + FileHelper.CSV_SPLIT + channel.getId() + FileHelper.CSV_SPLIT + channelTitle + FileHelper.CSV_SPLIT + channelDesc);
            } else {
                writer.write(channelEmail + FileHelper.CSV_SPLIT + FileHelper.CSV_SPLIT + FileHelper.CSV_SPLIT);
            }
            writer.newLine();
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

    public synchronized boolean addVideoTrace(String sourceId, Video video) throws IOException {
        BufferedWriter writer = null;
        boolean result = false;
        try {
            if(videos.containsKey(video.getId()))
                return false;

            writer = new BufferedWriter(new FileWriter(infoPath, true));
            //write data to csv
            //line 1, 2: source_id, video_id, video_title, video_desc, video_tags
            writer.write(FileHelper.toCsv(sourceId, video)); writer.newLine();

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

    public boolean login() throws IOException {
        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube");

        // Authorize the request.
        Credential credential = login(scopes, "updatevideo");
        return credential != null;
    }

    public boolean startService() throws IOException {
        if(Context.getDownloadService() == null)
            Context.setDownloadService(new DownloadService(null));
        if(!Context.getDownloadService().isRunning)
            Context.getDownloadService().start();

        if(Context.getUploadService() == null) {
            Context.setUploadService(new UploadService(getYouTube()));
            Context.getUploadService().setYouTubeService(Context.getYouTubeService());
        }
        if(!Context.getUploadService().isRunning){
            Context.getUploadService().setYouTubeService(service);
            Context.getUploadService().start();
        }
        return true;
    }

    private Credential login(List<String> scopes, String credentialDatastore) throws IOException {
        if(credential == null) {
            // Authorize the request.
            credential = Auth.authorize(scopes, credentialDatastore);

            loadConfig();
        }

        return credential;
    }

    public YouTube getYouTube() throws IOException {
        if(youtube == null) {
            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential)
                    .setApplicationName("uYouTube").build();
        }
        return youtube;
    }

    private void uploadVideo(String dirname) throws IOException {
        VideoDirectory channelDir = Context.getVideosDir(dirname);
        if(channelDir == null) {
            logger.error("Not found dir: {}", dirname);
            return;
        }

        Map<String, Video> downloadedVideos = channelDir.loadInfo();
        if(downloadedVideos == null || downloadedVideos.size() == 0) {
            logger.error("Not found video of dir: {}", dirname);
            return;
        }


    }

    public Channel getMyChannel() throws IOException {
        Map<String, Video> channelVideos = com.google.common.collect.Maps.newConcurrentMap();
        List<Channel> lstChannel = MyUploads.getMyChannelDetail(getYouTube());
        if (lstChannel != null && lstChannel.size() > 0)
            return lstChannel.get(0);

        return null;
    }

    public Map<String, Video> getMyUpload() throws IOException {
        Map<String, Video> channelVideos = com.google.common.collect.Maps.newConcurrentMap();
        List<Channel> lstChannel = MyUploads.getMyChannels(getYouTube());
        if(lstChannel != null)
            for (Channel channel : lstChannel) {
                List<Video> lstVideo = MyUploads.getChannelVideos(getYouTube(), channel);

                Iterator<Video> iter = lstVideo.iterator();
                while(iter.hasNext()) {
                    Video video = iter.next();
                    channelVideos.put(video.getId(), video);
                }
            }

        boolean needResave = channelVideos.size() < videos.size();
        //filter no longer exist video from config file
        Iterator<Video> iter = videos.values().iterator();
        while(iter.hasNext()) {
            Video video = iter.next();
            if(!channelVideos.containsKey(video.getId())) {
                logger.info("Remove videoId: {}, videoTitle: {}", video.getId(), video.getSnippet().getTitle());
                iter.remove();
                needResave = true;
            }
        }

        videos = channelVideos;
        //Resave config File
        if(needResave) {
            saveConfig(videos);
        }

        logger.info("Loaded channel: " + channel);
        logger.info("Total videos: " + channelVideos.size());
//        logger.debug("Channel videos: " + channelVideos);
        return videos;
    }

    public Result<Map<String, Video>> getNewVideos(String channelId){
        Result<Map<String, Video>> result = new Result<Map<String, Video>>();
        Map<String, Video> newVideos = new HashMap<String, Video>();

        if(channelId.length() == 0) {
            logger.error("Channel id invalid: {}", channelId);
            result.setErrorCode(ErrorCode.CHANNEL_ID_INVALID);
            return result;
        }

        try {
            //load downloaded video in directory
            VideoDirectory channelDir = Context.getVideosDir(channelId);
            Map<String, Video> downloadedVideos = channelDir.loadInfo();
            int total = 0;
            int downloaded = 0;

            //load video in channel from youtube
            List<Video> search = VideoSearch.search(null, channelId, Config.getInstance().getApiKey());
            if(search != null) {
                total = search.size();

                for (Video video : search) {
                    if(!downloadedVideos.containsKey(video.getId())) {
                        newVideos.put(video.getId(), video);
                        channelDir.addVideo(video);
                    } else
                        downloaded++;
                }
            }
            else
                logger.error("Not found video in channel: {}", channelId);

            logger.info(String.format("There are %d videos, downloaded: %d, remain: %d", total, downloaded, total - downloaded));
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setValue(newVideos);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    public ChannelSetting makeChannelSetting(String oldTitleReplace, String newTitleReplace, String oldDescReplace, String newDescReplace, String descAppend, String tags) {
        ChannelSetting setting = new ChannelSetting();

        if(!StringUtils.isEmpty(oldTitleReplace))
            setting.setOldTitleReplace(oldTitleReplace);
        if(!StringUtils.isEmpty(oldTitleReplace) || !StringUtils.isEmpty(newTitleReplace))
            setting.setNewTitleReplace(newTitleReplace);
        if(!StringUtils.isEmpty(oldDescReplace))
            setting.setOldDescReplace(oldDescReplace);
        if(!StringUtils.isEmpty(oldDescReplace) || !StringUtils.isEmpty(newDescReplace))
            setting.setNewDescReplace(newDescReplace);
        if(!StringUtils.isEmpty(descAppend))
            setting.setDescAppend(descAppend);

        if(!StringUtils.isEmpty(tags)) {
            List<String> lstTags = Lists.asList("", tags.split(","));
            setting.setDefaultTags(lstTags);
        }

        return setting;
    }

    public Video updateVideo(Video video, ChannelSetting setting) throws IOException {
        Preconditions.checkNotNull(setting, "setting is null, channelId: " + video.getSnippet().getChannelId());
        Video uploadVideo = FileHelper.makeUploadVideo(video, setting);
        return UpdateVideo.updateVideo(getYouTube(), uploadVideo);
    }

    public void download(Map<String, Video> videos) {
        if(videos != null && videos.size() > 0)
            for (Video video: videos.values()) {
                Context.getDownloadService().add(video);
            }
    }

    public void upload(Map<String, Video> videos) {
        if(videos != null && videos.size() > 0)
            for (Video video: videos.values()) {
                Context.getUploadService().add(video);
            }
    }

    public String getChannelEmail() {
        return channelEmail;
    }

    public void setChannelEmail(String channelEmail) {
        this.channelEmail = channelEmail;
        this.infoPath = FileHelper.makerChannelFileName(channelEmail);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Map<String, Video> getVideos() {
        return videos;
    }
}