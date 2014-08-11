package com.emmaguy.gifcast.util;

public class ImgurUrlParser {

    private static final String IMGUR_URL_BASE = "imgur.com/";

    public boolean isImgurUrl(String url) {
        return url.contains("imgur");
    }

    public boolean isImgurGallery(String url) {
        return isImgurUrl(url) && url.contains("gallery");
    }

    public boolean isImgurAlbum(String url) {
        return isImgurUrl(url) && url.contains("a/");
    }

    public String parseUrl(String url) {
        // strip off anything up to and including 'imgur.com/'
        String parsedUrl = url.substring(url.lastIndexOf(IMGUR_URL_BASE) + IMGUR_URL_BASE.length());

        if(isImgurAlbum(url)) {
            parsedUrl = parsedUrl.replace("a/", "album/");
        }

        // don't use .endsWith as could have ?5 or something after file type
        if(parsedUrl.contains(".jpg")) {
            parsedUrl = parsedUrl.substring(0, parsedUrl.lastIndexOf(".jpg"));
        }

        if(parsedUrl.contains(".jpeg")) {
            parsedUrl = parsedUrl.substring(0, parsedUrl.lastIndexOf(".jpeg"));
        }

        if(parsedUrl.contains(".png")) {
            parsedUrl = parsedUrl.substring(0, parsedUrl.lastIndexOf(".png"));
        }

        return parsedUrl;
    }
}
