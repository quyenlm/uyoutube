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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.mrmq.uyoutube.authenticate.Auth;
import com.mrmq.uyoutube.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class VideoSearch {
    private static final Logger logger = LoggerFactory.getLogger(UpdateVideo.class);

    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     */
    public static List<SearchResult> search(String queryTerm, String channelId, String apiKey) {
        List<SearchResult> searchResultList = new ArrayList<SearchResult>();
        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName(Config.getAppName()).build();

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the {{ Google Cloud Console }} for non-authenticated requests. See:
            // {{ https://cloud.google.com/console }}
            search.setKey(apiKey);
            if(queryTerm != null)
                search.setQ(queryTerm);
            if(channelId != null)
                search.setChannelId(channelId);


            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the application uses.
            search.setFields("items(id/kind,id/videoId,snippet,snippet/channelId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            boolean isEnd = false;
            String pageToken = null;
            while(!isEnd) {
                if(pageToken != null)
                    search.setPageToken(pageToken);

                // Call the API and print results.
                SearchListResponse searchResponse = search.execute();
                searchResultList.addAll(searchResponse.getItems());
                if(searchResponse.getNextPageToken() != null) {
                    pageToken = searchResponse.getNextPageToken();
                } else isEnd = true;
            }
        } catch (GoogleJsonResponseException e) {
            logger.error("There was a service error", e);
        } catch (IOException e) {
            logger.error("There was an IO error: ", e);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }

        return searchResultList;
    }

    public static List<Video> search(String termKey, String channelId) {
        List<Video> lstVideos = new ArrayList<Video>();
        List<SearchResult> search = search(termKey, channelId, Config.getApiKey());
        if(search != null) {
            Iterator<SearchResult> iteratorSearchResults = search.iterator();

            while (iteratorSearchResults.hasNext()) {
                SearchResult singleVideo = iteratorSearchResults.next();
                ResourceId rId = singleVideo.getId();

                // Confirm that the result represents a video. Otherwise, the
                // item will not contain a video ID.
                if (rId.getKind().equals("youtube#video")) {
                    Video video = new Video();
                    Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();

                    video.setId(rId.getVideoId());
                    VideoSnippet snippet = new VideoSnippet();
                    snippet.setTitle(singleVideo.getSnippet().getTitle());
                    snippet.setDescription(singleVideo.getSnippet().getDescription());
                    snippet.setChannelId(singleVideo.getSnippet().getChannelId());

                    video.setSnippet(snippet);

                    lstVideos.add(video);

                    logger.info(String.valueOf(video));
                }
            }
        }

        return lstVideos;
    }
}
