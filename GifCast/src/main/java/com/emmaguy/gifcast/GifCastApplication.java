package com.emmaguy.gifcast;

import android.app.Application;

import com.emmaguy.gifcast.data.LatestImagesRedditService;

import retrofit.RestAdapter;

public class GifCastApplication extends Application {
    private LatestImagesRedditService mLatestImagesRedditService;

    @Override public void onCreate() {
        super.onCreate();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://www.reddit.com/")
                .build();

        mLatestImagesRedditService = restAdapter.create(LatestImagesRedditService.class);
    }

    public LatestImagesRedditService getLatestImagesRedditService() {
        return mLatestImagesRedditService;
    }
}
