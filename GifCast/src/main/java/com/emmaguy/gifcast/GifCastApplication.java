package com.emmaguy.gifcast;

import android.app.Application;

import com.emmaguy.gifcast.data.api.ImgurService;
import com.emmaguy.gifcast.data.api.LatestImagesRedditService;
import com.emmaguy.gifcast.data.model.ImgurGalleryJson;
import com.emmaguy.gifcast.data.model.ImgurGalleryJsonDeserializer;
import com.google.gson.GsonBuilder;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

public class GifCastApplication extends Application {
    private LatestImagesRedditService mLatestImagesRedditService;
    private ImgurService mImgurService;
    private CachedRequestQueue mRequestQueue;

    @Override public void onCreate() {
        super.onCreate();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://www.reddit.com/")
                .build();
        mLatestImagesRedditService = restAdapter.create(LatestImagesRedditService.class);

        RestAdapter imgurRestAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.imgur.com/3/")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Client-Id " + getString(R.string.imgur_client_id));
                    }
                })
                .setConverter(new GsonConverter(new GsonBuilder().registerTypeAdapter(ImgurGalleryJson.class, new ImgurGalleryJsonDeserializer()).create()))
                //.setLogLevel(RestAdapter.LogLevel.FULL)
                //.setLog(new AndroidLog("GifCastTag-Retrofit"))
                .build();

        mImgurService = imgurRestAdapter.create(ImgurService.class);

        mRequestQueue = new CachedRequestQueue(this);
    }

    public CachedRequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public ImgurService getImgurService() {
        return mImgurService;
    }

    public LatestImagesRedditService getLatestImagesRedditService() {
        return mLatestImagesRedditService;
    }
}
