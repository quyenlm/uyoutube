package com.mrmq.uyoutube;

import com.google.api.services.youtube.YouTube;

import java.io.IOException;

/**
 * @author PSPC114
 * @version NTS
 * @description
 * @CrDate 12/2/2016
 * @Copyright Nextop Asia Limited. All rights reserved.
 */
public class MergeService extends Service {
    public MergeService(YouTube youtube) {
        super(youtube);
    }

    @Override
    protected void process() throws IOException {
        ProcessBuilder p = new ProcessBuilder();
        System.out.println(p.directory().getAbsoluteFile());
        System.out.println("Started EXE");
        p.command("D:\\House\\uapp\\download\\UC2tX6D14nJ3lowcg_VuYa0g\\ffmpeg.ext -i D:\\House\\uapp\\download\\_vKoAbwcx3Y.mp4 -i D:\\House\\uapp\\download\\_vKoAbwcx3Y.webm -c:a aac -c:v libx264 -strict -2 -c:s copy _vKoAbwcx3Y_Merged.mp4");

        p.start();
        System.out.println("Started EXE");
    }
}