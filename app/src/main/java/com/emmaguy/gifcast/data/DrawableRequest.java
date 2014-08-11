package com.emmaguy.gifcast.data;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
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
import com.emmaguy.gifcast.ui.BitmapRegionLoader;
import com.emmaguy.gifcast.util.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class DrawableRequest extends Request<Drawable> {
    /**
     * Socket timeout in milliseconds for image requests
     */
    private static final int IMAGE_TIMEOUT_MS = 1000;

    /**
     * Default number of retries for image requests
     */
    private static final int IMAGE_MAX_RETRIES = 2;

    /**
     * Default backoff multiplier for image requests
     */
    private static final float IMAGE_BACKOFF_MULT = 2f;

    /**
     * Decoding lock so that we don't decode more than one image at a time (to avoid OOM's)
     */
    private static final Object sDecodeLock = new Object();

    private final Response.Listener<Drawable> mListener;
    private final Resources mResources;

    public DrawableRequest(String url, Response.Listener<Drawable> listener, Response.ErrorListener errorListener, Resources r) {
        super(Method.GET, url, errorListener);
        mResources = r;
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
            if (Utils.isGif(getUrl())) {
                d = new GifDrawable(data);
            } else {
                d = parseBitmap(data);
            }
        } catch (IOException e) {
            Log.d("GifCastTag", "Failed to get url: " + getUrl());
            return Response.error(new ParseError(response));
        }

        return Response.success(d, HttpHeaderParser.parseCacheHeaders(response));
    }

    private Drawable parseBitmap(byte[] data) {
        BitmapRegionLoader bitmapRegionLoader = BitmapRegionLoader.newInstance(new ByteArrayInputStream(data));

        int width = bitmapRegionLoader.getWidth();
        int height = bitmapRegionLoader.getHeight();

        Rect rect = new Rect(0, 0, width, height);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = calculateSampleSize(height, 256);

        Drawable d = new BitmapDrawable(mResources, bitmapRegionLoader.decodeRegion(rect, options));
        bitmapRegionLoader.destroy();
        return d;
    }

    private static int calculateSampleSize(int rawHeight, int targetHeight) {
        int sampleSize = 1;
        while (rawHeight / (sampleSize * 2) > targetHeight) {
            sampleSize *= 2;
        }
        Log.d("GifCastTag", "rawheight: " + rawHeight + " samplesize: " + sampleSize);
        return sampleSize;
    }

    @Override
    protected void deliverResponse(Drawable response) {
        mListener.onResponse(response);
    }
}
