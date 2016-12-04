package com.mrmq.uyoutube;

import com.google.api.services.youtube.model.Video;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class AppStartup {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);
    private static YouTubeService service;

    public static void main(String[] args) {
        System.out.println(new File("").getAbsoluteFile());
        ProcessBuilder p = new ProcessBuilder();
        System.out.println(p.directory().getAbsoluteFile());

        System.out.println("Started EXE");
//        p.command("/House/uapp/download/UC2tX6D14nJ3lowcg_VuYa0g/ffmpeg.exe -i _vKoAbwcx3Y.mp4 -i _vKoAbwcx3Y.webm -c:a aac -c:v libx264 -strict -2 -c:s copy _vKoAbwcx3Y_Merged.mp4");
        p.command("D:\\House\\uapp\\download\\UC2tX6D14nJ3lowcg_VuYa0g\\ffmpeg.exe -i _vKoAbwcx3Y.mp4 -i _vKoAbwcx3Y.webm -c:a aac -c:v libx264 -strict -2 -c:s copy _vKoAbwcx3Y_Merged.mp4");

        try {
            p.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Started EXE");
//        String[] xmlConfigs = new String[]{"classpath:spring-uyoutube-context.xml"};
//        ApplicationContext ctx = new ClassPathXmlApplicationContext(xmlConfigs);
//
//        Scanner scanner = new Scanner(System.in);
//        String line;
//        printHelp();
//        while (scanner.hasNext()) {
//            line = scanner.nextLine();
//            if(StringUtils.isEmpty(line)) break;
//            if("exit".equals(line)) {
//                if(service != null) {
//                    if(Context.getDownloadService() != null) Context.getDownloadService().stopService();
//                    if(Context.getUploadService() != null) Context.getUploadService().stopService();
//                }
//                System.exit(0);
//            }
//
//            String[] array = line.split(" ", -1);
//            if("help".equals(array[0]))
//                printHelp();
//            else if("login".equals(array[0]) && array.length > 1)
//                login(array[1]);
//            else if("dl".equals(array[0]) && array.length > 1)
//                download(array[1]);
//            else if("ul".equals(array[0]) && array.length > 1)
//                upload(array[1]);
//            else logger.warn("Not support: {}", line);
//        }
    }

    private static void login(String email){
        try {
            service = new YouTubeService(email);
            Context.setYouTubeService(service);
            service.login();
            Context.getYouTubeService().startService();
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