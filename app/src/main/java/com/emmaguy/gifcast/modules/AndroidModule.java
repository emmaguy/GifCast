package com.emmaguy.gifcast.modules;

import android.app.Application;

import com.emmaguy.gifcast.GifCastApplication;
import com.emmaguy.gifcast.data.DrawableRequestQueue;
import com.emmaguy.gifcast.data.ImageLoader;
import com.emmaguy.gifcast.data.RedditImagesLoader;
import com.emmaguy.gifcast.data.RequestQueue;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {GifCastApplication.class}, library = true)
public class AndroidModule {
    private final GifCastApplication mApplication;

    public AndroidModule(GifCastApplication application) {
        mApplication = application;
    }

    @Provides
    public Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    public RequestQueue provideRequestQueue(Application app) {
        return new DrawableRequestQueue(app);
    }

    @Provides
    @Singleton
    public ImageLoader provideImageLoader(Application app) {
        return new RedditImagesLoader(app.getResources());
    }
}
