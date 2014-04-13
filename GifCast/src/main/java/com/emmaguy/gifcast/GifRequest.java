package com.emmaguy.gifcast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class GifRequest extends Request<GifDrawable> {
    /** Socket timeout in milliseconds for image requests */
    private static final int IMAGE_TIMEOUT_MS = 1000;

    /** Default number of retries for image requests */
    private static final int IMAGE_MAX_RETRIES = 2;

    /** Default backoff multiplier for image requests */
    private static final float IMAGE_BACKOFF_MULT = 2f;

    /** Decoding lock so that we don't decode more than one image at a time (to avoid OOM's) */
    private static final Object sDecodeLock = new Object();

    private final Response.Listener<GifDrawable> mListener;

    public GifRequest(String url, Response.Listener<GifDrawable> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(IMAGE_TIMEOUT_MS, IMAGE_MAX_RETRIES, IMAGE_BACKOFF_MULT));
        setShouldCache(true);
        mListener = listener;
    }

    @Override
    protected Response<GifDrawable> parseNetworkResponse(NetworkResponse response) {
        synchronized (sDecodeLock) {
            try {
                return parse(response);
            } catch (OutOfMemoryError e) {
                VolleyLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                return Response.error(new ParseError(e));
            }
        }
    }

    private Response<GifDrawable> parse(NetworkResponse response) {
        byte[] data = response.data;
        GifDrawable d = null;

        try {
            d = new GifDrawable(data);
        } catch (IOException e) {
            return Response.error(new ParseError(response));
        }

        return Response.success(d, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(GifDrawable response) {
        mListener.onResponse(response);
    }
}
