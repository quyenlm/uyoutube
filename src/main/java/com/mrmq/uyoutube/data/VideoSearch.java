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
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.mrmq.uyoutube.authenticate.Auth;
import com.mrmq.uyoutube.config.Config;
import com.mrmq.uyoutube.helper.Converter;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class VideoSearch extends Task {
    private static final Logger logger = LoggerFactory.getLogger(UpdateVideo.class);

    private static final long NUMBER_OF_VIDEOS_RETURNED = 50;

    public static List<Video> search(String queryTerm, String channelId, String apiKey) {
        return search(queryTerm, channelId, apiKey, null);
    }

    /**
     * Initialize a YouTube object to search for videos on YouTube. Then
     * display the name and thumbnail image of each video in the result set.
     *
     */
    public static List<Video> search(String queryTerm, String channelId, String apiKey, Function<Integer, Void> function) {
        Map<String, Video> searchResultList = new HashMap<String, Video>();
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
            search.setPart("snippet");
            search.setOrder("relevance");
            if(queryTerm != null)
                search.setQ(queryTerm);
            if(channelId != null)
                search.setChannelId(channelId);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the application uses.
            search.setFields("pageInfo,prevPageToken,nextPageToken,items(id/kind,id/videoId,snippet,snippet/channelId,snippet/title,snippet/description,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            boolean isFirst = true;
            String pageToken = null;
            ArrayList<String> loadedTokens = new ArrayList<String>();
            PageInfo pageInfo = null;

            Queue<String> queuePageTokens = new LinkedList<String>();

            while(isFirst || queuePageTokens.size() > 0) {
                isFirst = false;
                pageToken = queuePageTokens.poll();
                if(pageToken != null) {
                    search.setPageToken(pageToken);
                    loadedTokens.add(pageToken);
                }

                // Call the API and print results.
                SearchListResponse searchResponse = search.execute();
                if(pageInfo == null && searchResponse.getPageInfo() != null)
                    pageInfo = searchResponse.getPageInfo();

                if(searchResponse.getItems() != null && searchResponse.getItems().size() > 0) {
                    for (SearchResult searchResult : searchResponse.getItems()) {
                        Video video = Converter.convert(searchResult);
                        logger.info(String.valueOf(video));
                        searchResultList.put(video.getId(), video);
                    }

                    //notify percent download
                    if(function != null && pageInfo != null && pageInfo.getTotalResults() != null) {
                        function.apply((100 * searchResultList.size() / pageInfo.getTotalResults()));
                        logger.info("searchResult/total: {}/{}", searchResultList.size(), pageInfo.getTotalResults());
                    }
                }

                if(searchResponse.getNextPageToken() != null && !loadedTokens.contains(searchResponse.getNextPageToken()))
                    queuePageTokens.add(searchResponse.getNextPageToken());
//                if(searchResponse.getPrevPageToken() != null && !loadedTokens.contains(searchResponse.getPrevPageToken()))
//                    queuePageTokens.add(searchResponse.getPrevPageToken());
            }
        } catch (GoogleJsonResponseException e) {
            logger.error("There was a service error", e);
        } catch (IOException e) {
            logger.error("There was an IO error: ", e);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }

        return Lists.newArrayList(searchResultList.values());
    }

    @Override
    protected Object call() throws Exception {
        return null;
    }
}
