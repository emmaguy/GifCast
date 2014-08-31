package com.emmaguy.gifcast.test;

import com.emmaguy.gifcast.GifCastApplication;
import com.emmaguy.gifcast.data.ImageLoader;
import com.emmaguy.gifcast.data.RequestQueue;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {GifCastApplication.class}, library = true, overrides = true)
public class MockModule {
    public MockModule(GifCastApplication application) {

    }

    @Provides
    @Singleton
    public ImageLoader provideImageLoader() {
        return new com.emmaguy.gifcast.test.MockImageLoader();
    }

    @Provides
    @Singleton
    public RequestQueue provideRequestQueue() {
        return new com.emmaguy.gifcast.test.MockRequestQueue();
    }
}
