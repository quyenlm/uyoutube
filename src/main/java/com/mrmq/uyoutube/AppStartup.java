package com.mrmq.uyoutube;

import com.google.api.services.youtube.model.Video;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.Scanner;

public class AppStartup {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);
    private static YouTubeService service;

    public static void main(String[] args) {
        String[] xmlConfigs = new String[]{"classpath:spring-uyoutube-context.xml"};
        ApplicationContext ctx = new ClassPathXmlApplicationContext(xmlConfigs);

        Scanner scanner = new Scanner(System.in);
        String line;
        printHelp();
        while (scanner.hasNext()) {
            line = scanner.nextLine();
            if(StringUtils.isEmpty(line)) break;
            if("exit".equals(line)) {
                if(service != null) {
                    if(Context.getDownloadService() != null) Context.getDownloadService().stopService();
                    if(Context.getUploadService() != null) Context.getUploadService().stopService();
                }
                System.exit(0);
            }

            String[] array = line.split(" ", -1);
            if("help".equals(array[0]))
                printHelp();
            else if("login".equals(array[0]) && array.length > 1)
                login(array[1]);
            else if("dl".equals(array[0]) && array.length > 1)
                download(array[1]);
            else if("ul".equals(array[0]) && array.length > 1)
                upload(array[1]);
            else logger.warn("Not support: {}", line);
        }
    }

    private static void login(String email){
        try {
            service = new YouTubeService(email);
            Context.setYouTubeService(service);
            service.login();

            Context.setDownloadService(new DownloadService(null));
            Context.getDownloadService().start();

            Context.setUploadService(new UploadService(service.getYouTube()));
            Context.getUploadService().setYouTubeService(service);
            Context.getUploadService().start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void download(String channelId){
        try {
            Map<String, Video> videos = service.getNewVideos(channelId).getValue();
            service.download(videos);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void upload(String channelId){
        try {
            Map<String, Video> videos = service.getNewVideos(channelId).getValue();
            service.upload(videos);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void printHelp() {
        logger.info("login <email> : login");
        logger.info("dl <channelId> : download channel");
        logger.info("ul <channelId> : upload channel");
    }
}