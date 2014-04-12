package com.emmaguy.gifcast.data.model;

public class ImgurJson {
    public ImgurImage data;

    public class ImgurImage {
        public final String link;

        public ImgurImage(String link) {
            this.link = link;
        }
    }
}
