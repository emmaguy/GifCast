package com.emmaguy.gifcast.data;

import android.widget.ImageView;

public interface RequestQueue {
    void setDrawableOrAddRequest(final String url, final ImageView imageView);

    void cancelRequest(final String url);

    void setDataChangedListener(OnDataChangedListener dataChangedListener);

    boolean hasImageForUrl(final String url);
}
