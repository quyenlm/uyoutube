/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.mrmq.uyoutube.data;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.model.*;
import com.mrmq.uyoutube.authenticate.Auth;
import com.google.api.services.youtube.YouTube;
import com.google.common.collect.Lists;
import com.mrmq.uyoutube.helper.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Print a list of videos uploaded to the authenticated user's YouTube channel.
 *
 */
public class MyUploads {
    private static final Logger logger = LoggerFactory.getLogger(MyUploads.class);

    public static List<Channel> getMyChannels(YouTube youtube) throws IOException {
        YouTube.Channels.List channelRequest = youtube.channels().list("id,snippet");
        channelRequest.setMine(true);
        channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
        ChannelListResponse channelResult = channelRequest.execute();

        return channelResult.getItems();
    }

    public static List<Channel> getMyChannelDetail(YouTube youtube) throws IOException {
        YouTube.Channels.List channelRequest = youtube.channels().list("id,contentDetails,snippet");
        channelRequest.setMine(true);
        channelRequest.setFields("items/contentDetails,nextPageToken,pageInfo");
        ChannelListResponse channelResult = channelRequest.execute();

        return channelResult.getItems();
    }

    private static List<PlaylistItem> getPlaylistItems(YouTube youtube, Channel channel) throws IOException {
        String uploadPlaylistId = channel.getContentDetails().getRelatedPlaylists().getUploads();

        // Define a list to store items in the list of uploaded videos.
        List<PlaylistItem> playlistItemList = new ArrayList<PlaylistItem>();

        // Retrieve the playlist of the channel's uploaded videos.
        YouTube.PlaylistItems.List playlistItemRequest =
                youtube.playlistItems().list("id,contentDetails,snippet");
        playlistItemRequest.setPlaylistId(uploadPlaylistId);

        // Only retrieve data used in this application, thereby making
        // the application more efficient. See:
        // https://developers.google.com/youtube/v3/getting-started#partial
        playlistItemRequest.setFields(
                "items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");

        String nextToken = "";

        // Call the API one or more times to retrieve all items in the
        // list. As long as the API response returns a nextPageToken,
        // there are still more items to retrieve.
        do {
            playlistItemRequest.setPageToken(nextToken);
            PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

            playlistItemList.addAll(playlistItemResult.getItems());

            nextToken = playlistItemResult.getNextPageToken();
        } while (nextToken != null);

        return playlistItemList;
    }

    public static List<Video> getChannelVideos(YouTube youtube, Channel channel) throws IOException {
        List<Video> lstVideo = Lists.newArrayList();

        List<PlaylistItem> playList = getPlaylistItems(youtube, channel);
        if(playList != null) {
            Iterator<PlaylistItem> iter = playList.iterator();
            while(iter.hasNext()) {
                PlaylistItem playlistItem = iter.next();
                lstVideo.add(Converter.convert(playlistItem));
            }
        }

        return lstVideo;
    }
}
