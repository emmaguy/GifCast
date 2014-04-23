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

        return parsedUrl;
    }
}
