package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.emmaguy.gifcast.GifCastApplication;
import com.emmaguy.gifcast.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class ImageDetailActivity extends Activity {
    private LinearLayout linearLayout;
    private GifDrawable mPlaceholderGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_detail);

        initPlaceholderGif();
        linearLayout = (LinearLayout)findViewById(R.id.linearlayout);

        Bundle extras = getIntent().getExtras();
        String[] urls = extras.getStringArray("url");

        for(int i = urls.length - 1; i >= 0; i--) {

            final GifImageView imageView = new GifImageView(this);
            imageView.setImageDrawable(mPlaceholderGif);

            ((GifCastApplication)getApplication()).getRequestQueue().addRequest(urls[i], imageView);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);

            linearLayout.addView(imageView, 0, params);
        }
    }

    private void initPlaceholderGif() {
        if(mPlaceholderGif == null) {
            try {
                mPlaceholderGif = new GifDrawable(getAssets(), "spinner.gif");
            } catch (IOException e) {
                Log.e("GifCastTag", "Failed to create placeholder gif");
            }
        }
    }
}
