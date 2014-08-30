package com.emmaguy.gifcast;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
        GifCastApplication.class
}, includes = {
        DrawableRequestQueueModule.class
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
