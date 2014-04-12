package com.emmaguy.gifcast;

public class ImgurUrlParser {

    private static final String IMGUR_URL_BASE = "imgur.com/";

    public boolean isImgurUrl(String url) {
        return url.contains("imgur");
    }

    public boolean isImgurGallery(String url) {
        return isImgurUrl(url) && url.contains("gallery");
    }

    public String parseUrl(String url) {
        // strip off anything up to and including 'imgur.com/'
        return url.substring(url.lastIndexOf(IMGUR_URL_BASE) + IMGUR_URL_BASE.length());
    }
}
