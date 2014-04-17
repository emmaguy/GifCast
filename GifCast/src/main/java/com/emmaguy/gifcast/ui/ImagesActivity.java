package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.emmaguy.gifcast.EndlessScrollListener;
import com.emmaguy.gifcast.GifCastApplication;
import com.emmaguy.gifcast.ImgurUrlParser;
import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.api.ImgurService;
import com.emmaguy.gifcast.data.api.LatestImagesRedditService;
import com.emmaguy.gifcast.data.model.ImgurGalleryJson;
import com.emmaguy.gifcast.data.model.ImgurJson;
import com.emmaguy.gifcast.data.model.RedditNewImagesJson;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ImagesActivity extends Activity implements AdapterView.OnItemClickListener {

    private final NewRedditImagesLoader mImagesLoader = new NewRedditImagesLoader();

    private GridView mGridView;
    private View mLoadingFooter;
    private ImagesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mLoadingFooter = findViewById(R.id.footer);
        mGridView = (GridView) findViewById(R.id.gridview);
        mAdapter = new ImagesAdapter(this, getApp().getRequestQueue());
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnScrollListener(new EndlessScrollListener(1) {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Toast.makeText(ImagesActivity.this, "moar!", Toast.LENGTH_SHORT).show();

                mLoadingFooter.setVisibility(View.VISIBLE);

                String afterId = ((Image)mAdapter.getItem(mAdapter.getCount() - 1)).getRedditId();
                mImagesLoader.load(getApp(), "", afterId);
            }
        });

        mImagesLoader.setTargetActivity(this);
        mImagesLoader.load(getApp(), null, null);

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(getResources().getColor(R.color.hot_pink));
    }

    private GifCastApplication getApp() {
        return (GifCastApplication) getApplication();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Image img = (Image)mAdapter.getItem(i);

        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra("url", img.getImageUrls());
        startActivity(intent);
    }

    public static class NewRedditImagesLoader {
        private ImagesActivity mActivity;
        private final ImgurUrlParser mImgurUrlParser = new ImgurUrlParser();

        public void setTargetActivity(ImagesActivity a) {
            mActivity = a;
        }

        public void load(final GifCastApplication app, final String before, final String after) {
            final ImgurService imgurService = app.getImgurService();

            LatestImagesRedditService imagesService = app.getLatestImagesRedditService();
            imagesService.getNewImagesInSubreddit("gifs", 20, before, after, new Callback<RedditNewImagesJson>() {
                @Override
                public void success(RedditNewImagesJson data, Response response) {
                    if(mActivity == null || data == null || data.data == null || data.data.children == null) return;

                    List<Image> urls = new ArrayList<Image>();
                    for(RedditNewImagesJson.RedditData.RedditImageData i : data.data.children) {
                        final String url = i.data.url;

                        if(isImage(url)) {
                            urls.add(new Image(i.data.name, i.data.title));
                        } else if(mImgurUrlParser.isImgurUrl(url)) {

                            final String imgurUrl = mImgurUrlParser.parseUrl(url);
                            if(mImgurUrlParser.isImgurGallery(url)) {

                                final Image galleryImg = new Image(i.data.name, i.data.title);
                                urls.add(galleryImg);
                                imgurService.getImgurImagesInGallery(imgurUrl, new Callback<ImgurGalleryJson>() {
                                    @Override
                                    public void success(ImgurGalleryJson imgurGalleryJson, Response response) {
                                        if(mActivity == null || imgurGalleryJson == null) return;

                                        galleryImg.updateUrls(imgurGalleryJson.data);
                                        mActivity.mAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Log.d("GifCastTag", "Error in gallery: " + url + " msg: " + error.getMessage());
                                    }
                                });
                            } else {
                                final Image image = new Image(i.data.name, i.data.title);
                                urls.add(image);
                                imgurService.getImgurImageUrl(imgurUrl, new Callback<ImgurJson>() {
                                    @Override
                                    public void success(ImgurJson imgurJson, Response response) {
                                        if(mActivity == null || imgurJson == null) return;

                                        image.updateUrl(imgurJson.data.link);
                                        mActivity.mAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Log.d("GifCastTag", "Error getting single imgur link: " + error.getMessage() + " url: " + url);
                                    }
                                });
                            }

                        } else {
                            Log.d("GifCastTag", "Ignoring url: " + url);
                        }
                    }

                    mActivity.mAdapter.addImageUrls(urls);
                    mActivity.mLoadingFooter.setVisibility(View.GONE);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("GifCastTag", error.getMessage(), error);

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