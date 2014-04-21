package com.emmaguy.gifcast.data;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.emmaguy.gifcast.util.Utils;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class DrawableRequest extends Request<Drawable> {
    /** Socket timeout in milliseconds for image requests */
    private static final int IMAGE_TIMEOUT_MS = 1000;

    /** Default number of retries for image requests */
    private static final int IMAGE_MAX_RETRIES = 2;

    /** Default backoff multiplier for image requests */
    private static final float IMAGE_BACKOFF_MULT = 2f;

    /** Decoding lock so that we don't decode more than one image at a time (to avoid OOM's) */
    private static final Object sDecodeLock = new Object();

    private final Response.Listener<Drawable> mListener;

    public DrawableRequest(String url, Response.Listener<Drawable> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(IMAGE_TIMEOUT_MS, IMAGE_MAX_RETRIES, IMAGE_BACKOFF_MULT));
        setShouldCache(true);
        mListener = listener;
    }

    @Override
    protected Response<Drawable> parseNetworkResponse(NetworkResponse response) {
        synchronized (sDecodeLock) {
            try {
                return parse(response);
            } catch (OutOfMemoryError e) {
                VolleyLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                return Response.error(new ParseError(e));
            }
        }
    }

    private Response<Drawable> parse(NetworkResponse response) {
        byte[] data = response.data;
        Drawable d = null;

        try {
            if(Utils.isGif(getUrl())) {
                d = new GifDrawable(data);
            } else {
                d = new BitmapDrawable(BitmapFactory.decodeByteArray(data, 0, data.length));
            }
        } catch (IOException e) {
            Log.d("GifCastTag", "Failed to get url: " + getUrl());
            return Response.error(new ParseError(response));
        }

        return Response.success(d, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(Drawable response) {
        mListener.onResponse(response);
    }
}
