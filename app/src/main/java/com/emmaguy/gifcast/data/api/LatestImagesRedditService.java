package com.emmaguy.gifcast.data.api;

import com.emmaguy.gifcast.data.api.model.RedditNewImagesJson;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface LatestImagesRedditService {
    @GET("/r/{subreddit}/new.json?sort=new")
    void getNewImagesInSubreddit(@Path("subreddit") String subreddit,
                                 @Query("limit") Integer limit,
                                 @Query("before") String before,
                                 @Query("after") String after,
                                 Callback<RedditNewImagesJson> callback);
}