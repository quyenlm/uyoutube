package com.mrmq.uyoutube.helper;


import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;

public class Converter {
    public static Video convert(PlaylistItem playlistItem) {
        Video video = new Video();
        video.setId(playlistItem.getContentDetails().getVideoId());

        VideoSnippet snippet = new VideoSnippet();
        snippet.setTitle(playlistItem.getSnippet().getTitle());
        snippet.setDescription(playlistItem.getSnippet().getDescription());
        snippet.setPublishedAt(playlistItem.getSnippet().getPublishedAt());
        snippet.setChannelId(playlistItem.getSnippet().getChannelId());
        video.setSnippet(snippet);

        return video;
    }
}
