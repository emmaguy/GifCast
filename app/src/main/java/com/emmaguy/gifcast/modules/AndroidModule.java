package com.emmaguy.gifcast.modules;

import android.app.Application;

import com.emmaguy.gifcast.GifCastApplication;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
        GifCastApplication.class
}, includes = {
        DrawableRequestQueueModule.class,
        ImageLoaderModule.class
})
public class AndroidModule {
    private final GifCastApplication mApplication;

    public AndroidModule(GifCastApplication application) {
        mApplication = application;
    }

    @Provides
    public Application provideApplication() {
        return mApplication;
    }
}
