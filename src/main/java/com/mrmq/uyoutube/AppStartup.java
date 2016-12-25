package com.mrmq.uyoutube;

import com.google.api.services.youtube.model.Video;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;
import java.util.Map;
import java.util.Scanner;

public class AppStartup {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);
    private static YouTubeService service;

    public static void main(String[] args) {
//        System.out.println(new File("").getAbsoluteFile());
//        ProcessBuilder p = new ProcessBuilder("G:\\ffmpeg\\bin\\ffmpeg.exe -i KPpb26Uz70k.mp4 -i KPpb26Uz70k.webm -c:a aac -c:v libx264 -strict -2 -c:s copy KPpb26Uz70k_out.mp4");
//        p.directory(new File("G:\\ffmpeg\\bin\\"));
//
//        try {
//            Process d = p.start();
////            Process p = Runtime.getRuntime().exec("G:\\ffmpeg\\bin\\ffmpeg.exe -i G:\\ffmpeg\\bin\\KPpb26Uz70k.mp4 -i G:\\ffmpeg\\bin\\KPpb26Uz70k.webm -c:a aac -c:v libx264 -strict -2 -c:s copy G:\\ffmpeg\\bin\\KPpb26Uz70k_Merged.mp4", new String[]{""} , new File("G:\\ffmpeg\\bin\\"));
//            BufferedReader reader = new BufferedReader(new InputStreamReader(d.getInputStream()));
//            String msg;
//            while((msg = reader.readLine()) != null)
//                System.out.println(msg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Started EXE");
//        try {
////            String[] cmd = { "C:\\E.M. TVCC\\TVCC.exe", "-f E:\\TestVideo\\01.avi", "-o E:\\OutputFiles\\target.3gp" };
////            String[] cmd = {"G:\\ffmpeg\\bin\\ffmpeg.exe", "-i G:\\ffmpeg\\bin\\KPpb26Uz70k.mp4 -i G:\\ffmpeg\\bin\\KPpb26Uz70k.webm -c:a aac -c:v libx264 -strict -2 -c:s copy G:\\ffmpeg\\bin\\KPpb26Uz70k_Merged.mp4"};
////            Process p = Runtime.getRuntime().exec(cmd);
//
//
//            OutputStream stdin = null;
//            InputStream stderr = null;
//            InputStream stdout = null;
//
////            ProcessBuilder pb = new ProcessBuilder("G:\\ffmpeg\\bin\\ffmpeg.exe -i G:\\ffmpeg\\bin\\KPpb26Uz70k.mp4 -i G:\\ffmpeg\\bin\\KPpb26Uz70k.webm -c:a aac -c:v libx264 -strict -2 -c:s copy G:\\ffmpeg\\bin\\KPpb26Uz70k_Merged.mp4");
////            ProcessBuilder pb = new ProcessBuilder("G:\\ffmpeg\\bin\\ffmpeg", "-i", "G:\\ffmpeg\\bin\\KPpb26Uz70k.mp4", "-i", "G:\\ffmpeg\\bin\\KPpb26Uz70k.webm", "-c:a", "aac", "-c:v", "libx264", "-strict", "-2", "-c:s", "copy", "G:\\ffmpeg\\bin\\KPpb26Uz70k_Merged.mp4");
//            ProcessBuilder pb = new ProcessBuilder("G:\\ffmpeg\\bin\\a.bat");
//            pb.directory(new File("G:\\ffmpeg\\bin\\"));
//
//            Process process = pb.start();
//            stdin = process.getOutputStream();
//            stderr = process.getErrorStream();
//            stdout = process.getInputStream();
//
//            // "write" the parms into stdin
////            stdin.write("ffmpeg.exe -i KPpb26Uz70k.mp4 -i KPpb26Uz70k.webm -c:a aac -c:v libx264 -strict -2 -c:s copy KPpb26Uz70k_Merged.mp4".getBytes());
//            stdin.flush();
//            stdin.close();
//
//            String line;
//            // clean up if any output in stdout
//            BufferedReader brCleanUp = new BufferedReader (new InputStreamReader (stdout));
//            while ((line = brCleanUp.readLine ()) != null) {
//                System.out.println ("[Stdout] " + line);
//            }
//            brCleanUp.close();
//
//            // clean up if any output in stderr
//            brCleanUp = new BufferedReader (new InputStreamReader (stderr));
//            while ((line = brCleanUp.readLine ()) != null) {
//                System.out.println ("[Stderr] " + line);
//            }
//            brCleanUp.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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