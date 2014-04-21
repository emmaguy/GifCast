package com.emmaguy.gifcast.data.api.model;

public class ImgurJson {
    public ImgurImage data;

    public class ImgurImage {
        public final String link;
        public final String title;

        public ImgurImage(String link, String title) {
            this.link = link;
            this.title = title;
        }
    }
}
