package com.emmaguy.gifcast.data;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.api.ImgurService;
import com.emmaguy.gifcast.data.api.LatestImagesRedditService;
import com.emmaguy.gifcast.data.api.model.ImgurGalleryJson;
import com.emmaguy.gifcast.data.api.model.ImgurGalleryJsonDeserializer;
import com.emmaguy.gifcast.data.api.model.ImgurJson;
import com.emmaguy.gifcast.data.api.model.RedditNewImagesJson;
import com.emmaguy.gifcast.util.ImgurUrlParser;
import com.emmaguy.gifcast.util.Utils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class RedditImagesLoader {
    private final ImgurUrlParser mImgurUrlParser = new ImgurUrlParser();
    private List<Image> mImages = new ArrayList<Image>();
    private OnRedditItemsChanged mListener;

    private ImgurService mImgurService;
    private LatestImagesRedditService mLatestImagesRedditService;

    public RedditImagesLoader(final Resources r) {
        mLatestImagesRedditService = new RestAdapter.Builder()
                .setEndpoint("http://www.reddit.com/")
                .build()
                .create(LatestImagesRedditService.class);

        mImgurService = new RestAdapter.Builder()
                .setEndpoint("https://api.imgur.com/3/")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Client-Id " + r.getString(R.string.imgur_client_id));
                    }
                })
                .setConverter(new GsonConverter(new GsonBuilder().registerTypeAdapter(ImgurGalleryJson.class, new ImgurGalleryJsonDeserializer()).create()))
                .build().create(ImgurService.class);
    }

    public void setImagesRequesterListener(OnRedditItemsChanged listener) {
        mListener = listener;
    }

    public void load(final Context context, final String before, final String after) {
        List<String> selectedSubReddits = Utils.selectedSubReddits(context.getApplicationContext());

        if (selectedSubReddits.size() <= 0) {
            Toast.makeText(context.getApplicationContext(), R.string.no_subreddits, Toast.LENGTH_SHORT).show();
            return;
        }

        mLatestImagesRedditService.getNewImagesInSubreddit(TextUtils.join("+", selectedSubReddits), 20, before, after, new Callback<RedditNewImagesJson>() {
            @Override
            public void success(RedditNewImagesJson data, Response response) {
                if (data == null || data.data == null || data.data.children == null)
                    return;

                List<Image> images = getImages(data.data.children);

                mImages.addAll(images);
                mListener.onNewItemsAdded(images);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("GifCastTag", error.getMessage(), error);

                Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Image> getImages(List<RedditNewImagesJson.RedditData.RedditImageData> data) {
        List<Image> images = new ArrayList<Image>();

        for (RedditNewImagesJson.RedditData.RedditImageData i : data) {
            final String url = i.data.url;

            final Image img = new Image(i.data.name, i.data.title, i.data.subreddit, i.data.over_18);
            img.setThumbnailUrl(i.data.thumbnail);

            if (Utils.isImage(url)) {
                img.updateUrl(url);
                images.add(img);
            } else if (mImgurUrlParser.isImgurUrl(url)) {
                final String imgurUrl = mImgurUrlParser.parseUrl(url);
                if (mImgurUrlParser.isImgurGallery(url)) {
                    requestImgurGalleryImages(img, imgurUrl);
                } else if (mImgurUrlParser.isImgurAlbum(url)) {
                    requestImgurGalleryImages(img, imgurUrl);
                } else {
                    requestImgurImage(img, imgurUrl);
                }
                images.add(img);
            } else {
                Log.d("GifCastTag", "Ignoring url: " + url);
            }
        }
        return images;
    }

    private void requestImgurImage(final Image img, final String imgurUrl) {
        mImgurService.getImgurImageUrl(imgurUrl, new Callback<ImgurJson>() {
            @Override
            public void success(ImgurJson imgurJson, Response response) {
                if (imgurJson == null) return;

                img.updateUrl(imgurJson.data.link);
                mListener.onItemsChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("GifCastTag", "Error getting single imgur link, url: " + imgurUrl);
            }
        });
    }

    private void requestImgurGalleryImages(final Image img, final String imgurUrl) {
        mImgurService.getImgurImagesInGallery(imgurUrl, new Callback<ImgurGalleryJson>() {
            @Override
            public void success(ImgurGalleryJson imgurGalleryJson, Response response) {
                if (imgurGalleryJson == null) return;

                img.updateUrls(imgurGalleryJson.data);
                mListener.onItemsChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("GifCastTag", "Error getting imgur gallery url: " + imgurUrl + " msg: " + error.getMessage());
            }
        });
    }

    public interface OnRedditItemsChanged {
        void onNewItemsAdded(List<Image> images);

        void onItemsChanged();
    }
}
