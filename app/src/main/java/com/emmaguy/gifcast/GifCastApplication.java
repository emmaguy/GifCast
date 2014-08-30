package com.emmaguy.gifcast;

import android.app.Application;

import com.emmaguy.gifcast.data.ImageLoader;
import com.emmaguy.gifcast.data.RequestQueue;
import com.emmaguy.gifcast.modules.AndroidModule;

import java.util.Arrays;
import java.util.List;

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

        mObjectGraph = ObjectGraph.create(getModules().toArray());
        mObjectGraph.inject(this);
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new AndroidModule(this));
    }

    public RequestQueue requestQueue() {
        return mDrawableRequestQueue;
    }

    public ImageLoader imageLoader() {
        return mImageLoader;
    }

    public ObjectGraph createScopedGraph(Object... modules) {
        return mObjectGraph.plus(modules);
    }
}
