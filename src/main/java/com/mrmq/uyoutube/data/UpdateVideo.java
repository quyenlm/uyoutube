/*
 * Copyright (c) 2013 Google Inc.
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
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Update a video by adding a keyword tag to its metadata. The demo uses the
 * YouTube Data API (v3) and OAuth 2.0 for authorization.
 */
public class UpdateVideo {
    private static final Logger logger = LoggerFactory.getLogger(UpdateVideo.class);

    /**
     * Add a keyword tag to a video that the user specifies. Use OAuth 2.0 to
     * authorize the API request.
     */
    public static Video updateVideo(YouTube youtube, String videoId, List<String> tags) {
        Video videoResponse = null;
        try {
            // Call the YouTube Data API's youtube.videos.list method to
            // retrieve the resource that represents the specified video.
            YouTube.Videos.List listVideosRequest = youtube.videos().list("snippet").setId(videoId);
            VideoListResponse listResponse = listVideosRequest.execute();

            // Since the API request specified a unique video ID, the API
            // response should return exactly one video. If the response does
            // not contain a video, then the specified video ID was not found.
            List<Video> videoList = listResponse.getItems();
            if (videoList.isEmpty()) {
                logger.error("Can't find a video with ID: " + videoId);
                return null;
            }

            // Extract the snippet from the video resource.
            Video video = videoList.get(0);
            VideoSnippet snippet = video.getSnippet();

            // Preserve any tags already associated with the video. If the
            // video does not have any tags, create a new array. Append the
            // provided tag to the list of tags associated with the video.
            if(tags != null)
                snippet.setTags(tags);
            else if(snippet.getTags() != null)
                snippet.getTags().clear();

            // Update the video resource by calling the videos.update() method.
            YouTube.Videos.Update updateVideosRequest = youtube.videos().update("snippet", video);
            videoResponse = updateVideosRequest.execute();

            // Print information from the updated resource.
            logger.info("\n================== Returned Video ==================\n");
            logger.info("  - Title: " + videoResponse.getSnippet().getTitle());
            logger.info("  - Tags: " + videoResponse.getSnippet().getTags());
            return videoResponse;
        } catch (GoogleJsonResponseException e) {
            logger.error("GoogleJsonResponseException code: " + e.getDetails().getCode() + " : "  + e.getDetails().getMessage());
        } catch (IOException e) {
            logger.error("IOException: " + e.getMessage(), e);
        } catch (Throwable t) {
            logger.error("Throwable: " + t.getMessage(), t);
        }

        return videoResponse;
    }
}
