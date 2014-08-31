package com.emmaguy.gifcast;

import android.app.Application;

import com.emmaguy.gifcast.data.ImageLoader;
import com.emmaguy.gifcast.data.RequestQueue;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class GifCastApplication extends Application {
    private ObjectGraph mObjectGraph;

    @Inject
    RequestQueue mDrawableRequestQueue;

    @Inject
    ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(Modules.list(this));
        mObjectGraph.inject(this);
    }

    public RequestQueue requestQueue() {
        return mDrawableRequestQueue;
    }

    public ImageLoader imageLoader() {
        return mImageLoader;
    }
}
