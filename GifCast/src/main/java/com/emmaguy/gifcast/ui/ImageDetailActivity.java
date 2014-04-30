package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.GifCastApplication;
import com.emmaguy.gifcast.ui.views.FitScreenWidthImageView;
import com.readystatesoftware.systembartint.SystemBarTintManager;


public class ImageDetailActivity extends Activity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_detail);

        linearLayout = (LinearLayout)findViewById(R.id.linearlayout);

        Bundle extras = getIntent().getExtras();
        String title = extras.getString("title");
        String[] urls = extras.getStringArray("url");

        getActionBar().setTitle(title);

        for(int i = 0; i < urls.length; i++) {

            final String url = urls[i];
            final FitScreenWidthImageView imageView = new FitScreenWidthImageView(this);
            imageView.setImageResource(R.drawable.animated_progress);
            imageView.setTag(url);

            ((GifCastApplication)getApplication()).getRequestQueue().setDrawableOrAddRequest(url, imageView);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            linearLayout.addView(imageView, params);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.hot_pink));
    }
}
