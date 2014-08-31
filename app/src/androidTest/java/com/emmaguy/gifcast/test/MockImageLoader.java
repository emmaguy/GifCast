package com.emmaguy.gifcast.test;

import android.content.Context;
import android.util.Log;

import com.emmaguy.gifcast.data.Image;
import com.emmaguy.gifcast.data.ImageLoader;
import com.emmaguy.gifcast.data.OnRedditItemsChanged;

import java.util.ArrayList;
import java.util.List;

public class MockImageLoader implements ImageLoader {
    private List<Image> mImages = new ArrayList<Image>();

    public MockImageLoader() {
        for (int i = 0; i < 20; i++) {
            Image image = new Image("" + i, "Awesome Item: " + i, "gifs", false);
            image.updateUrl("" + i);
            mImages.add(image);
        }
    }

    @Override
    public List<Image> getAllImages() {
        Log.d("emma", "items count TestImageLoader: " + mImages.size());
        return mImages;
    }

    @Override
    public void setImagesRequesterListener(OnRedditItemsChanged listener) {

    }

    @Override
    public void load(Context context, String before, String after) {

    }
}