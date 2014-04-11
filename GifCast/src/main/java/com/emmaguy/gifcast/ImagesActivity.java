package com.emmaguy.gifcast;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.emmaguy.gifcast.data.LatestImagesRedditService;
import com.emmaguy.gifcast.data.RedditData;
import com.emmaguy.gifcast.data.RedditImageData;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ImagesActivity extends Activity {

    private final RedditImagesLoader mImagesLoader = new RedditImagesLoader();
    private GridView mGridView;
    private ImagesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGridView = (GridView) findViewById(R.id.gridview);
        mAdapter = new ImagesAdapter(this);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mImagesLoader.setTargetActivity(this);
        mImagesLoader.load((GifCastApplication)getApplication());
    }

    @Override
    protected void onPause() {
        mImagesLoader.setTargetActivity(null);

        super.onPause();
    }

    public static class RedditImagesLoader {
        ImagesActivity mActivity;

        public void setTargetActivity(ImagesActivity a) {
            mActivity = a;
        }

        public void load(GifCastApplication app) {
            LatestImagesRedditService imagesService = app.getLatestImagesRedditService();
            imagesService.getNewImagesInSubreddit("gifs", new Callback<RedditData>() {
                @Override
                public void success(RedditData data, Response response) {
                    if(mActivity == null || data == null || data.data == null || data.data.children == null) return;

                    List<String> urls = new ArrayList<String>();
                    for(RedditImageData i : data.data.children) {
                        urls.add(i.data.url);
                    }

                    mActivity.mAdapter.setImageUrls(urls);
                }

                @Override
                public void failure(RetrofitError error) {
                    if(mActivity == null) return;

                    Log.e("Emma", error.getMessage(), error);
                    Toast.makeText(mActivity, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
