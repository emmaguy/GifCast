package com.emmaguy.gifcast.data;

import android.graphics.drawable.Drawable;
import android.util.LruCache;

public class DrawableLruCache extends LruCache<String, Drawable> {
    public DrawableLruCache(int maxSize) {
        super(getDefaultLruCacheSize());
    }

    public DrawableLruCache() {
        super(getDefaultLruCacheSize());
    }

    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }
}
