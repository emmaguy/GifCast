package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.DrawableRequestQueue;
import com.emmaguy.gifcast.data.GifCastApplication;
import com.emmaguy.gifcast.data.Image;
import com.emmaguy.gifcast.util.Utils;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ImagesActivity extends Activity implements AdapterView.OnItemClickListener, GifCastApplication.RedditImagesLoader.OnRedditItemsChanged, DrawableRequestQueue.OnDataChangedListener {
    private static final String GRIDVIEW_INSTANCE_STATE = "gridview_scroll_position";

    private GridView mGridView;
    private SmoothProgressBar mProgressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_images);

        initialise(savedInstanceState);
    }

    private void initialise(final Bundle savedInstanceState) {
        mProgressBar = (SmoothProgressBar) findViewById(R.id.progressbar);
        mGridView = (GridView) findViewById(R.id.gridview);

         mGridView.setAdapter(new ImagesAdapter(this, getApp().getRequestQueue(), shouldHideNSFW()));
         setImagesFromMemoryOrRetrieve(savedInstanceState);

        if (canEnableEndlessScrolling()) {
            enableEndlessScrolling();
        }

        mGridView.setOnItemClickListener(this);

        Utils.tintActionBar(this);
    }

    private void setImagesFromMemoryOrRetrieve(final Bundle savedInstanceState) {
        List<Image> images = getApp().getAllImages();
        getApp().setImagesRequesterListener(this);
        getApp().setDataChangedListener(this);

        if (images.size() <= 0) {
            retrieveLatestImages();
        } else {
            setImages(savedInstanceState, images);
        }
    }

    private void retrieveLatestImages() {
        showAndStartAnimatingProgressBar();
        getApp().requestItems("", "");
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

    private void enableEndlessScrolling() {
        mGridView.setOnScrollListener(new EndlessScrollListener(1) {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Toast.makeText(ImagesActivity.this, "moar!", Toast.LENGTH_SHORT).show();

                int count = getAdapter().getCount();
                if (count > 0) {
                    showAndStartAnimatingProgressBar();
                    String afterId = ((Image) getAdapter().getItem(count - 1)).getRedditId();
                    getApp().requestItems("", afterId);
                }
            }
        });
    }

    private void showAndStartAnimatingProgressBar() {
        mProgressBar.progressiveStart();
    }

    private boolean canEnableEndlessScrolling() {
        if(getIntent().getExtras() != null) {
            return getIntent().getExtras().getBoolean("ENABLE_ENDLESS");
        }

        return true;
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
        for(String s : savedSubreddits) {
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
                        retrieveLatestImages();
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
                        if(subreddit.length() > 0) {
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