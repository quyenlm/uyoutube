package com.mrmq.uyoutube.helper;


import com.google.api.services.youtube.model.*;

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

    public static Video convert(SearchResult singleVideo) {
        Video video = new Video();
        ResourceId rId = singleVideo.getId();

        // Confirm that the result represents a video. Otherwise, the
        // item will not contain a video ID.
        if (rId.getKind().equals("youtube#video")) {
            Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

            video.setId(rId.getVideoId());
            VideoSnippet snippet = new VideoSnippet();
            snippet.setTitle(singleVideo.getSnippet().getTitle());
            snippet.setDescription(singleVideo.getSnippet().getDescription());
            snippet.setChannelId(singleVideo.getSnippet().getChannelId());
            snippet.setPublishedAt(singleVideo.getSnippet().getPublishedAt());
            video.setSnippet(snippet);
        }

        return video;
    }
}
