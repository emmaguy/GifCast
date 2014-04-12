package com.emmaguy.gifcast.ui;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Image {
    public List<String> urls;

    public Image() {
        urls = new ArrayList<String>();
    }

    public Image(final String url) {
        urls = new ArrayList<String>();
        urls.add(url);
    }

    public void updateUrl(String url) {
        urls.clear();
        urls.add(url);
    }

    public void updateUrls(List<String> urls) {
        this.urls = urls;

        Log.d("Emma", "urls now: " + urls.size());
    }

    public String thumbnailUrl() {
        return urls.get(0);
    }

    public int getNumberOfImages() {
        return urls.size();
    }

    public String[] getImageUrls() {
        return urls.toArray(new String[urls.size()]);
    }
}