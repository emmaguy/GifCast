package com.emmaguy.gifcast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ImagesActivity extends Activity implements AdapterView.OnItemClickListener {

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
        mGridView.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String url = (String)mAdapter.getItem(i);

        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
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
                        if(isImage(i.data.url)) {
                            Log.d("Emma", i.data.url);
                            urls.add(i.data.url);
                        }
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

        private static boolean isImage(String pictureFileName) {
            return pictureFileName.endsWith(".png")
                    || pictureFileName.endsWith(".gif")
                    || pictureFileName.endsWith(".jpg")
                    || pictureFileName.endsWith(".jpeg");
        }
    }
}
