package com.emmaguy.gifcast.data.api;

import com.emmaguy.gifcast.data.model.RedditNewImagesJson;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface LatestImagesRedditService {
    @GET("/r/{subreddit}/new.json?sort=new&limit=20")
    void getNewImagesInSubreddit(@Path("subreddit") String subreddit, Callback<RedditNewImagesJson> callback);
}
