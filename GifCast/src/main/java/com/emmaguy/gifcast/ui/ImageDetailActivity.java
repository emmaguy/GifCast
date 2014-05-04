package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.GifCastApplication;
import com.emmaguy.gifcast.ui.views.FitScreenWidthImageView;
import com.emmaguy.gifcast.util.Utils;

public class ImageDetailActivity extends Activity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        linearLayout = (LinearLayout) findViewById(R.id.linearlayout);

        Bundle extras = getIntent().getExtras();
        String subReddit = extras.getString("subreddit");
        String title = extras.getString("title");
        String[] urls = extras.getStringArray("url");

        getActionBar().setTitle("/r/" + subReddit + ": " + title);

        for (int i = 0; i < urls.length; i++) {

            final String url = urls[i];
            final FitScreenWidthImageView imageView = new FitScreenWidthImageView(this);
            imageView.setImageResource(R.drawable.animated_progress);
            imageView.setTag(url);

            ((GifCastApplication) getApplication()).getRequestQueue().setDrawableOrAddRequest(url, imageView);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

            linearLayout.addView(imageView, params);
        }

        Utils.tintActionBar(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // previous instance of ImagesActivity will be restored in prior state rather than recreated
                Intent parent = NavUtils.getParentActivityIntent(this);
                parent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, parent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
