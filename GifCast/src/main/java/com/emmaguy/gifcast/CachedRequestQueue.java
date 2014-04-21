package com.emmaguy.gifcast;

import android.content.Context;
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

import pl.droidsonroids.gif.GifDrawable;

public class CachedRequestQueue {
    private RequestQueue mRequestQueue;
    private GifDrawableLruCache mCache;
    private HashMap<String, String> mRequestedUrls = new HashMap<String, String>();

    public CachedRequestQueue(Context c) {
        mRequestQueue = Volley.newRequestQueue(c, new OkHttpStack());
        mCache = new GifDrawableLruCache();
    }

    public void addRequest(final String url, final ImageView imageView) {
        GifDrawable cachedGif = mCache.get(url);
        if(cachedGif != null) {
            setGifDrawable(url, imageView, cachedGif);
            return;
        }

        // if a request has already begun, don't add it again
        if(mRequestedUrls.containsKey(url)) {
            return;
        }
        mRequestedUrls.put(url, "");

        GifRequest r = new GifRequest(url, new Response.Listener<GifDrawable>() {
            @Override
            public void onResponse(GifDrawable response) {
                setGifDrawable(url, imageView, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GifCastTag", "Failed to get gif drawable: " + error.getMessage(), error);
            }
        });

        r.setTag(url);
        mRequestQueue.add(r);
    }

    public void cancelRequest(final String url) {
        mRequestedUrls.remove(url);
        mRequestQueue.cancelAll(url);
    }

    private void setGifDrawable(String url, ImageView imageView, GifDrawable gif) {
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