package com.mrmq.uyoutube;

import com.mrmq.uyoutube.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.IOException;
import java.util.Arrays;

public class AppStartup {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    public static void main(String[] args) {
        logger.info("start with args: " + Arrays.toString(args));
        YouTubeService service = new YouTubeService("p@gmail.com");
        AppManagedDownload d = new AppManagedDownload();
        d.downloadVideo("https://www.youtube.com/watch?v=CAos34CvvBU", "", "G:\\VIDEOS\\test\\");

//        String[] xmlConfigs = new String[]{"classpath:spring-uyoutube-context.xml"};
//        ApplicationContext ctx = new ClassPathXmlApplicationContext(xmlConfigs);


    }
}