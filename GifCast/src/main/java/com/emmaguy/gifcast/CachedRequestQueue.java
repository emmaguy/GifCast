package com.emmaguy.gifcast;

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

public class CachedRequestQueue {
    private RequestQueue mRequestQueue;
    private DrawableLruCache mCache;
    private HashMap<String, String> mRequestedUrls = new HashMap<String, String>();

    public CachedRequestQueue(Context c) {
        mRequestQueue = Volley.newRequestQueue(c, new OkHttpStack());
        mCache = new DrawableLruCache();
    }

    public void addRequest(final String url, final ImageView imageView) {
        Drawable cachedGif = mCache.get(url);
        if(cachedGif != null) {
            setDrawable(url, imageView, cachedGif);
            return;
        }

        // if a request has already begun, don't add it again
        if(mRequestedUrls.containsKey(url)) {
            return;
        }
        mRequestedUrls.put(url, "");

        DrawableRequest r = new DrawableRequest(url, new Response.Listener<Drawable>() {
            @Override
            public void onResponse(Drawable response) {
                setDrawable(url, imageView, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GifCastTag", "Failed to get drawable: " + error.getMessage(), error);
            }
        });

        r.setTag(url);
        mRequestQueue.add(r);
    }

    public void cancelRequest(final String url) {
        mRequestedUrls.remove(url);
        mRequestQueue.cancelAll(url);
    }

    private void setDrawable(String url, ImageView imageView, Drawable gif) {
        mCache.put(url, gif);
        mRequestedUrls.remove(url);

        String imageViewUrl = (String)imageView.getTag();
        if(!TextUtils.isEmpty(imageViewUrl) && imageViewUrl.equals(url)) {
            imageView.setImageDrawable(gif);
        } else {
            Log.d("GifCastTag", "Not setting: " + url + " because " + imageViewUrl);
        }
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