package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.emmaguy.gifcast.GifCastApplication;
import com.emmaguy.gifcast.R;

import pl.droidsonroids.gif.GifImageView;


public class ImageDetailActivity extends Activity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_detail);

        linearLayout = (LinearLayout)findViewById(R.id.linearlayout);

        Bundle extras = getIntent().getExtras();
        String[] urls = extras.getStringArray("url");

        for(int i = urls.length - 1; i >= 0; i--) {

            final GifImageView imageView = new GifImageView(this);
            imageView.setImageResource(R.drawable.animated_progress);
            imageView.setTag(urls[i]);

            ((GifCastApplication)getApplication()).getRequestQueue().addRequest(urls[i], imageView);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);

            linearLayout.addView(imageView, 0, params);
        }
    }
}
