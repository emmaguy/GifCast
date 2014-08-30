package com.emmaguy.gifcast.modules;

import android.app.Application;

import com.emmaguy.gifcast.data.RequestQueue;
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
    public RequestQueue provideDrawableRequestQueue(Application app) {
        return new DrawableRequestQueue(app);
    }
}
