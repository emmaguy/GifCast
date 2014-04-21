package com.emmaguy.gifcast.data.model;

import java.util.List;

// e.g. http://www.reddit.com//r/gifs/new.json
public class RedditNewImagesJson {
    public RedditData data;

    public RedditNewImagesJson(RedditData data) {
        this.data = data;
    }

    public class RedditData {
        public List<RedditImageData> children;

        public RedditData(List<RedditImageData> children) {
            this.children = children;
        }

        public class RedditImageData {
            public RedditImage data;

            public RedditImageData(RedditImage data) {
                this.data = data;
            }

            public class RedditImage {
                public final String url;
                public final String title;

                public final String name;
                public final String thumbnail;

                public final long created_utc;
                public final boolean is_self;
                public final boolean over_18;

                public RedditImage(String url, boolean is_self, boolean over_18, long created_utc, String title, String name, String thumbnail) {
                    this.url = url;
                    this.is_self = is_self;
                    this.over_18 = over_18;
                    this.created_utc = created_utc;
                    this.title = title;
                    this.name = name;
                    this.thumbnail = thumbnail;
                }
            }
        }
    }
}
