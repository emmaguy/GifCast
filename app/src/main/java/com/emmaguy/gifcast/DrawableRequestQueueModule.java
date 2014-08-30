package com.emmaguy.gifcast;

import android.app.Application;

import com.emmaguy.gifcast.data.DrawableRequestQueue;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class DrawableRequestQueueModule {
    @Provides
    @Singleton
    public DrawableRequestQueue provideDrawableRequestQueue(Application app) {
        return new DrawableRequestQueue(app);
    }
}
