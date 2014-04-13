package com.emmaguy.gifcast;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.atomic.AtomicInteger;

import pl.droidsonroids.gif.GifDrawable;

public class CachedRequestQueue {
    private RequestQueue mRequestQueue;

    public CachedRequestQueue(Context c) {
        mRequestQueue = Volley.newRequestQueue(c);
    }

    public void cancelRequest(final String url) {
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return request.getUrl().equals(url);
            }
        });

    }
    private AtomicInteger counter = new AtomicInteger(0);

    public void addRequest(final String url, final ImageView imageView) {
        Cache.Entry d = mRequestQueue.getCache().get(url);
        final int debugNumber = counter.getAndIncrement();
        Log.d("GifCastTag", "Requesting: " + url + " d: " + (d==null) + " x: " + debugNumber);

        GifRequest r = new GifRequest(url, new Response.Listener<GifDrawable>() {
            @Override
            public void onResponse(GifDrawable response) {
                Cache.Entry dd = mRequestQueue.getCache().get(url);
                Log.d("GifCastTag", "onResponse: " + url + " d: " + (dd==null)+ " x: " + debugNumber);

                imageView.setImageDrawable(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("GifCastTag", "Failed to get gif drawable: " + error.getMessage(), error);
            }
        });
        mRequestQueue.add(r);
    }
}
