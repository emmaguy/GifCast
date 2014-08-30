package com.emmaguy.gifcast.data;

import android.content.Context;

import java.util.List;

public interface ImageLoader {
    List<Image> getAllImages();

    void setImagesRequesterListener(OnRedditItemsChanged listener);
    void load(Context context, String before, String after);
}