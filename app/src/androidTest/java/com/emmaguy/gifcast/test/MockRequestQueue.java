package com.emmaguy.gifcast.test;

import android.widget.ImageView;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.OnDataChangedListener;
import com.emmaguy.gifcast.data.RequestQueue;

public class MockRequestQueue implements RequestQueue {

    @Override
    public void setDrawableOrAddRequest(String url, ImageView imageView) {
        imageView.setImageResource(R.drawable.ic_launcher);
    }

    @Override
    public void cancelRequest(String url) {

    }

    @Override
    public void setDataChangedListener(OnDataChangedListener dataChangedListener) {

    }

    @Override
    public boolean hasImageForUrl(String url) {
        return true;
    }
}
