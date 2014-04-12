package com.emmaguy.gifcast;

import android.util.Log;

public class ImgurUrlParser {

    private static final String IMGUR_URL_BASE = "imgur.com/";

    public boolean isImgurUrl(String url) {
        return url.contains("imgur");
    }

    public String parseUrl(String url) {
        // strip off anything up to and including 'imgur.com/'
        url = url.substring(url.lastIndexOf(IMGUR_URL_BASE) + IMGUR_URL_BASE.length());

        if(url.contains("gallery")) {
            url = "https://api.imgur.com/3/" + url;
        } else {
            url = "https://api.imgur.com/3/image/" + url;
        }

        Log.d("GifCast", "parsed url to: " + url);

        return url;
    }
}
