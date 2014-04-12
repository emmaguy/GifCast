package com.emmaguy.gifcast.data;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;

public interface LatestImagesRedditService {
    @GET("/r/{subreddit}/new.json?sort=new&limit=100")
    void getNewImagesInSubreddit(@Path("subreddit") String subreddit, Callback<RedditNewImagesJson> callback);
}
