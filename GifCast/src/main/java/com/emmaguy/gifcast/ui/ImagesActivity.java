package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.emmaguy.gifcast.GifCastApplication;
import com.emmaguy.gifcast.ImgurUrlParser;
import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.api.ImgurService;
import com.emmaguy.gifcast.data.api.LatestImagesRedditService;
import com.emmaguy.gifcast.data.model.ImgurGalleryJson;
import com.emmaguy.gifcast.data.model.ImgurJson;
import com.emmaguy.gifcast.data.model.RedditNewImagesJson;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ImagesActivity extends Activity implements AdapterView.OnItemClickListener {

    private final NewRedditImagesLoader mImagesLoader = new NewRedditImagesLoader();

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
        mImagesLoader.load((GifCastApplication) getApplication());
    }

    @Override
    protected void onPause() {
        mImagesLoader.setTargetActivity(null);

        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Image img = (Image)mAdapter.getItem(i);

        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra("url", img.thumbnailUrl());
        startActivity(intent);
    }

    public static class NewRedditImagesLoader {
        private ImagesActivity mActivity;
        private final ImgurUrlParser mImgurUrlParser = new ImgurUrlParser();

        public void setTargetActivity(ImagesActivity a) {
            mActivity = a;
        }

        public void load(GifCastApplication app) {
            final ImgurService imgurService = app.getImgurService();

            LatestImagesRedditService imagesService = app.getLatestImagesRedditService();
            imagesService.getNewImagesInSubreddit("gifs", new Callback<RedditNewImagesJson>() {
                @Override
                public void success(RedditNewImagesJson data, Response response) {
                    if(mActivity == null || data == null || data.data == null || data.data.children == null) return;

                    List<Image> urls = new ArrayList<Image>();
                    for(RedditNewImagesJson.RedditData.RedditImageData i : data.data.children) {
                        final String url = i.data.url;

                        if(isImage(url)) {
                            urls.add(new Image(url));
                        } else if(mImgurUrlParser.isImgurUrl(url)) {

                            final String imgurUrl = mImgurUrlParser.parseUrl(url);
                            if(mImgurUrlParser.isImgurGallery(url)) {

                                final Image galleryImg = new Image(imgurUrl);
                                imgurService.getImgurImagesInGallery(imgurUrl, new Callback<ImgurGalleryJson>() {
                                    @Override
                                    public void success(ImgurGalleryJson imgurGalleryJson, Response response) {
                                        if(mActivity == null || imgurGalleryJson == null) return;

                                        galleryImg.updateUrls(imgurGalleryJson.data);
                                        mActivity.mAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Log.d("Emma", "Error in gallery: " + url + " msg: " + error.getMessage());
                                    }
                                });
                            } else {
                                final Image image = new Image(imgurUrl);
                                Log.d("Emma", "img: " + imgurUrl);
                                imgurService.getImgurImageUrl(imgurUrl, new Callback<ImgurJson>() {
                                    @Override
                                    public void success(ImgurJson imgurJson, Response response) {
                                        if(mActivity == null || imgurJson == null) return;

                                        image.updateUrl(imgurJson.data.link);
                                        mActivity.mAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Log.d("Emma", "Error getting single imgur link: + " + error.getMessage());
                                    }
                                });
                            }

                        } else {
                            Log.d("GifCast", "Ignoring url: " + url);
                        }
                    }

                    mActivity.mAdapter.setImageUrls(urls);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("Emma", error.getMessage(), error);

                    if(mActivity == null) return;

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