//package com.mrmq.uyoutube.helper;
//
//
//import com.google.api.services.youtube.model.Channel;
//import com.google.api.services.youtube.model.Video;
//import com.google.api.services.youtube.model.VideoSnippet;
//import com.google.common.base.Strings;
//import com.google.common.collect.Lists;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//
//public class FileHelper {
//    private static final Logger logger = LoggerFactory.getLogger(FileHelper.class);
//
//    public static void d(String fileName){
//        BufferedReader reader = null;
//        try {
//            File configFile = new File(ownerEmail);
//            if (configFile.exists()) {
//                //read data from csv
//                //line 1: email, channel_id, channel_name, tags
//                //line 2: source_id, video_id, video_title, video_desc, video_tags
//
//                String line;
//                String[] arrTmp;
//                reader = new BufferedReader(new FileReader(configFile));
//                boolean isFirst = true;
//                while ((line = reader.readLine()) != null) {
//                    arrTmp = line.split(",", -1);
//
//                    if(isFirst) {
//                        isFirst = false;
//                        channel = new Channel();
//                        channel.setId(arrTmp[1]); //Id
//                    } else {
//                        Video video = new Video();
//                        video.setId(arrTmp[1]); //source_id
//
//                        VideoSnippet snippet = new VideoSnippet();
//
//                        snippet.setTitle(arrTmp[2]);
//                        snippet.setDescription(arrTmp[3]);
//
//                        if(!Strings.isNullOrEmpty(arrTmp[4])) {
//                            String[] arrTags = arrTmp[4].split("<>");
//                            snippet.setTags(Lists.newArrayList(arrTags));
//                        }
//
//                        videos.put(arrTmp[0], video);
//                    }
//                }
//            } else {
//                configFile.createNewFile();
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        } finally {
//            if(reader != null)
//                reader.close();
//        }
//    }
//}
