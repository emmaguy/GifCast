package com.emmaguy.gifcast.modules;

import android.app.Application;

import com.emmaguy.gifcast.data.ImageLoader;
import com.emmaguy.gifcast.data.RedditImagesLoader;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        complete = false,
        library = true
)
public class ImageLoaderModule {

    @Provides
    @Singleton
    public ImageLoader provideImageLoader(Application app) {
        return new RedditImagesLoader(app.getResources());
    }
}
