package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.GifCastApplication;
import com.emmaguy.gifcast.data.Image;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ImagesActivity extends Activity implements AdapterView.OnItemClickListener, GifCastApplication.RedditImagesLoader.OnRedditItemsChanged {
    private static final String GRIDVIEW_INSTANCE_STATE = "gridview_scroll_position";

    private GridView mGridView;
    private ImagesAdapter mAdapter;
    private SmoothProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mProgressBar = (SmoothProgressBar) findViewById(R.id.progressbar);
        mGridView = (GridView) findViewById(R.id.gridview);
        mAdapter = new ImagesAdapter(this, getApp().getRequestQueue(), shouldHideNSFW());

        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        List<Image> images = getApp().getAllImages();
        getApp().setImagesRequesterListener(this);

        if (images.size() <= 0) {
            getApp().requestItems("", "");
        } else {
            mProgressBar.progressiveStop();
            mAdapter.addImages(images);
            mAdapter.notifyDataSetChanged();
        }

        mGridView.setOnScrollListener(new EndlessScrollListener(1) {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Toast.makeText(ImagesActivity.this, "moar!", Toast.LENGTH_SHORT).show();

                if (mAdapter.getCount() > 0) {
                    String afterId = ((Image) mAdapter.getItem(mAdapter.getCount() - 1)).getRedditId();
                    getApp().requestItems("", afterId);
                    mProgressBar.progressiveStart();
                    mProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.hot_pink));

        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(GRIDVIEW_INSTANCE_STATE);
            mGridView.setSelection(position);
        }
    }

    private GifCastApplication getApp() {
        return (GifCastApplication) getApplication();
    }

    private boolean shouldHideNSFW() {
        return getPreferences(MODE_PRIVATE).getBoolean("hide_nsfw", true);
    }

    private void updateHideNSFW(boolean hideNSFW) {
        getPreferences(MODE_PRIVATE).edit().putBoolean("hide_nsfw", hideNSFW).apply();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putInt(GRIDVIEW_INSTANCE_STATE, mGridView.getFirstVisiblePosition());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_images, menu);

        menu.findItem(R.id.hide_nsfw).setChecked(shouldHideNSFW());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.hide_nsfw) {
            boolean shouldHideNSFW = !shouldHideNSFW();
            updateHideNSFW(shouldHideNSFW);

            mAdapter.toggleNSFWFilter(shouldHideNSFW);
            item.setChecked(shouldHideNSFW);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Image img = (Image) mAdapter.getItem(i);

        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra("title", img.getTitle());
        intent.putExtra("url", img.getImageUrls());
        startActivity(intent);
    }

    @Override
    public void onNewItemsAdded(List<Image> images) {
        mAdapter.addImages(images);
        mAdapter.notifyDataSetChanged();
        mProgressBar.progressiveStop();
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onItemsChanged() {
        mAdapter.notifyDataSetChanged();
    }
}