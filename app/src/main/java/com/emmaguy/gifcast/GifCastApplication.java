package com.emmaguy.gifcast;

import android.app.Application;

import com.emmaguy.gifcast.data.DrawableRequestQueue;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class GifCastApplication extends Application {
    private ObjectGraph mObjectGraph;
    @Inject DrawableRequestQueue mDrawableRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();

        mObjectGraph = ObjectGraph.create(getModules().toArray());
        mObjectGraph.inject(this);
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new AndroidModule(this));
    }

    public DrawableRequestQueue requestQueue() {
        return mDrawableRequestQueue;
    }
}
