package com.emmaguy.gifcast;

import android.util.LruCache;

import pl.droidsonroids.gif.GifDrawable;

public class GifDrawableLruCache extends LruCache<String, GifDrawable> {
    public GifDrawableLruCache(int maxSize) {
        super(getDefaultLruCacheSize());
    }

    public GifDrawableLruCache() {
        super(getDefaultLruCacheSize());
    }

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }
}
