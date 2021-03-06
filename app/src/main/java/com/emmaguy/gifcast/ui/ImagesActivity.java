package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.emmaguy.gifcast.GifCastApplication;
import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.Image;
import com.emmaguy.gifcast.data.ImageLoader;
import com.emmaguy.gifcast.data.OnDataChangedListener;
import com.emmaguy.gifcast.data.OnRedditItemsChanged;
import com.emmaguy.gifcast.data.RequestQueue;
import com.emmaguy.gifcast.util.Utils;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ImagesActivity extends Activity implements AdapterView.OnItemClickListener, OnRedditItemsChanged, OnDataChangedListener {
    private static final String GRIDVIEW_INSTANCE_STATE = "gridview_scroll_position";

    private GridView mGridView;
    private SmoothProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_images);

        initialise(savedInstanceState);
    }

    private void initialise(Bundle savedInstanceState) {
        mProgressBar = (SmoothProgressBar) findViewById(R.id.progressbar);
        mGridView = (GridView) findViewById(R.id.gridview);

        GifCastApplication app = (GifCastApplication) getApplication();
        RequestQueue requestQueue = app.requestQueue();
        ImageLoader imageLoader = app.imageLoader();

        mGridView.setAdapter(new ImagesAdapter(this, requestQueue, shouldHideNSFW()));

        requestQueue.setDataChangedListener(this);
        imageLoader.setImagesRequesterListener(this);

        Log.d("emma", "items count ImagesActivity: " + imageLoader.getAllImages().size());
        if (imageLoader.getAllImages().size() <= 0) {
            retrieveLatestImages(imageLoader);
        } else {
            // used on orientation changed
            setImages(savedInstanceState, imageLoader.getAllImages());
        }

        enableEndlessScrolling(imageLoader);

        mGridView.setOnItemClickListener(this);

        Utils.tintActionBar(this);
    }

    public void requestItems(final ImageLoader imageLoader, final String before, final String after) {
        imageLoader.load(this, before, after);
    }

    private void retrieveLatestImages(ImageLoader imageLoader) {
        showAndStartAnimatingProgressBar();
        requestItems(imageLoader, "", "");
    }

    private void enableEndlessScrolling(final ImageLoader imageLoader) {
        mGridView.setOnScrollListener(new EndlessScrollListener(1) {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Toast.makeText(ImagesActivity.this, "moar!", Toast.LENGTH_SHORT).show();

                int count = getAdapter().getCount();
                if (count > 0) {
                    showAndStartAnimatingProgressBar();
                    String afterId = ((Image) getAdapter().getItem(count - 1)).getRedditId();
                    requestItems(imageLoader, "", afterId);
                }
            }
        });
    }

    private void showAndStartAnimatingProgressBar() {
        mProgressBar.progressiveStart();
    }

    private boolean shouldHideNSFW() {
        return getPreferences(MODE_PRIVATE).getBoolean("hide_nsfw", true);
    }

    private void updateHideNSFW(boolean hideNSFW) {
        getPreferences(MODE_PRIVATE).edit().putBoolean("hide_nsfw", hideNSFW).apply();
    }

    private void setImages(final Bundle savedInstanceState, List<Image> images) {
        final ImagesAdapter adapter = getAdapter();
        adapter.addImages(images);
        adapter.setFilteringCompleteListener(new ImagesAdapter.OnFilteringComplete() {
            @Override
            public void onFilteringComplete() {
                setScrollPositionFromInstanceState(savedInstanceState);
                adapter.setFilteringCompleteListener(null);
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void setScrollPositionFromInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int position = savedInstanceState.getInt(GRIDVIEW_INSTANCE_STATE);
            mGridView.setSelection(position);
        }
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

            ((ImagesAdapter) mGridView.getAdapter()).toggleNSFWFilter(shouldHideNSFW);
            item.setChecked(shouldHideNSFW);
            return true;
        } else if (id == R.id.subreddit) {
            showSelectSubredditsDialog();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSelectSubredditsDialog() {
        final List<String> savedSubreddits = Utils.allSubReddits(getApplication().getApplicationContext());
        final List<String> selectedSubreddits = Utils.selectedSubReddits(getApplication().getApplicationContext());
        final boolean[] selected = new boolean[savedSubreddits.size()];

        int i = 0;
        for (String s : savedSubreddits) {
            selected[i] = selectedSubreddits.contains(s);

            i++;
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_subreddits))
                .setMultiChoiceItems(savedSubreddits.toArray(new String[savedSubreddits.size()]), selected, new DialogInterface.OnMultiChoiceClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        selected[i] = b;
                        ((AlertDialog) dialogInterface).getListView().setItemChecked(i, b);
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<String> selectedSrs = new ArrayList<String>();

                        int index = 0;
                        for (boolean b : selected) {
                            if (b) {
                                selectedSrs.add(savedSubreddits.get(index));
                            }
                            index++;
                        }
                        Utils.saveSubreddits(getApplication().getApplicationContext(), savedSubreddits);
                        Utils.saveSelectedSubreddits(getApplication().getApplicationContext(), selectedSrs);

                        getAdapter().clearAdapter();
                        retrieveLatestImages(((GifCastApplication) getApplication()).imageLoader());
                    }
                })
                .setNeutralButton(R.string.add_subreddit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showAddSubredditDialog();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void showAddSubredditDialog() {
        final EditText input = new EditText(this);

        new AlertDialog.Builder(this)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String subreddit = input.getText().toString().trim();
                        if (subreddit.length() > 0) {
                            Utils.addSubreddit(getApplication().getApplicationContext(), subreddit);
                        }
                        showSelectSubredditsDialog();
                    }
                })
                .setTitle(R.string.add_subreddit)
                .setMessage(R.string.enter_subreddit_name)
                .setView(input)
                .create()
                .show();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Image img = (Image) mGridView.getAdapter().getItem(i);

        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra("title", img.getTitle());
        intent.putExtra("url", img.getImageUrls());
        intent.putExtra("subreddit", img.getSubReddit());
        startActivity(intent);
    }

    @Override
    public void onNewItemsAdded(List<Image> images) {
        final ImagesAdapter adapter = (ImagesAdapter) mGridView.getAdapter();
        adapter.addImages(images);
        adapter.notifyDataSetChanged();

        hideAndStopAnimatingProgressBar();
    }

    private void hideAndStopAnimatingProgressBar() {
        mProgressBar.progressiveStop();
    }

    @Override
    public void onItemsChanged() {
        getAdapter().notifyDataSetChanged();
    }

    private ImagesAdapter getAdapter() {
        return ((ImagesAdapter) mGridView.getAdapter());
    }

    @Override
    public void onDataChanged() {
        getAdapter().notifyDataSetChanged();
    }
}