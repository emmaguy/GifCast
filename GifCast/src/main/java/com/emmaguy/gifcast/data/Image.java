package com.emmaguy.gifcast.data;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Image {
    private final String mTitle;
    private final String mRedditId;
    private final String mSubReddit;
    private final boolean mIsNSFW;

    private String mThumbnailUrl;

    public List<String> mUrls;

    public Image(final String id, final String title, final String subReddit, final boolean isNSFW) {
        mUrls = new ArrayList<String>();

        mSubReddit = subReddit;
        mRedditId = id;
        mTitle = title;
        mIsNSFW = isNSFW;
    }

    public void updateUrl(String url) {
        mUrls.clear();
        mUrls.add(url);
    }

    public void updateUrls(List<String> urls) {
        this.mUrls = urls;
    }

    public String thumbnailUrl() {
        if(hasThumbnail()) {
            return mThumbnailUrl;
        }

        return mUrls.get(0);
    }

    public String[] getImageUrls() {
        return mUrls.toArray(new String[mUrls.size()]);
    }

    public String getRedditId() {
        return mRedditId;
    }

    public String getTitle() {
        return mTitle;
    }

    public boolean isNSFW() {
        return mIsNSFW;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        if(!thumbnailUrl.equals("default")) {
            mThumbnailUrl = thumbnailUrl;
        }
    }

    public boolean hasThumbnail() {
        return !TextUtils.isEmpty(mThumbnailUrl);
    }

    public boolean hasUrl() {
        return mUrls.size() > 0;
    }

    public String getSubReddit() {
        return mSubReddit;
    }
}