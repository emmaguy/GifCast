package com.emmaguy.gifcast.data;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class DrawableRequestQueue {
    private RequestQueue mRequestQueue;
    private DrawableLruCache mCache;
    private HashMap<String, String> mRequestedUrls = new HashMap<String, String>();

    public DrawableRequestQueue(Context c) {
        mRequestQueue = Volley.newRequestQueue(c, new OkHttpStack());
        mCache = new DrawableLruCache();
    }

    public void setDrawableOrAddRequest(final String url, final ImageView imageView) {
        Drawable drawable = mCache.get(url);
        if(drawable != null) {
            Log.d("GifCastTag", "found in cache: " + url);
            imageView.setImageDrawable(drawable);
            return;
        }

        // if a request has already begun, don't add it again
        if(mRequestedUrls.containsKey(url)) {
            Log.d("GifCastTag", "found existing request for: " + url);
            return;
        }
        mRequestedUrls.put(url, "");

        DrawableRequest r = new DrawableRequest(url, new Response.Listener<Drawable>() {
            @Override
            public void onResponse(Drawable response) {
                mCache.put(url, response);
                mRequestedUrls.remove(url);
                Log.d("GifCastTag", "saving to cache: " + url);

                setDrawableIfMatchingTagUrl(url, imageView, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GifCastTag", "Failed to get drawable: ", error);
            }
        });

        r.setTag(url);
        mRequestQueue.add(r);
        Log.d("GifCastTag", "adding req: " + url);
    }

    public void cancelRequest(final String url) {
        Log.d("GifCastTag", "cancelling: " + url);
        mRequestedUrls.remove(url);
        mRequestQueue.cancelAll(url);
    }

    private void setDrawableIfMatchingTagUrl(String url, ImageView imageView, Drawable drawable) {
        String imageViewUrl = (String)imageView.getTag();
        if(!TextUtils.isEmpty(imageViewUrl) && imageViewUrl.equals(url)) {
            Log.d("GifCastTag", "setting: " + url);
            imageView.setImageDrawable(drawable);
        } else {
            Log.d("GifCastTag", "Not setting: " + url + " because " + imageViewUrl);
        }
    }

    public boolean hasImageForUrl(String url) {
        return mCache.get(url) != null;
    }

    private class OkHttpStack extends HurlStack {
        private final OkHttpClient client;

        public OkHttpStack() {
            this(new OkHttpClient());
        }

        public OkHttpStack(OkHttpClient client) {
            if (client == null) {
                throw new NullPointerException("Client must not be null.");
            }
            this.client = client;
        }

        @Override protected HttpURLConnection createConnection(URL url) throws IOException {
            return client.open(url);
        }
    }
}