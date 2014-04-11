package com.emmaguy.gifcast.data;

public class RedditImage {
    public final String url;
    public final boolean is_self;
    public final boolean over_18;

    public RedditImage(String url, boolean is_self, boolean over_18) {
        this.url = url;
        this.is_self = is_self;
        this.over_18 = over_18;
    }
}
