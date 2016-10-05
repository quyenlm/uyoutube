package com.mrmq.uyoutube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;

@Controller
@EnableAutoConfiguration
public class AppStartup {
    private static final Logger logger = LoggerFactory.getLogger(AppStartup.class);

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        logger.info("start with args: " + Arrays.toString(args));
        YouTubeService service = new YouTubeService(args[0]);

        try {
            service.loadConfig();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
}