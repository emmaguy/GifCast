package com.emmaguy.gifcast.ui;

/*
 * Adapted from Roman Nurik's Muzei: https://github.com/romannurik/muzei
 *
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import static android.graphics.BitmapFactory.Options;

public class BitmapRegionLoader {
    private boolean mValid = false;
    private int mOriginalWidth;
    private int mOriginalHeight;
    private Rect mTempRect = new Rect();
    private InputStream mInputStream;
    private volatile BitmapRegionDecoder mBitmapRegionDecoder;

    public static BitmapRegionLoader newInstance(InputStream in) {
        if (in == null) {
            return null;
        }

        BitmapRegionLoader loader = new BitmapRegionLoader(in);
        if (loader.mValid) {
            return loader;
        }

        return null;
    }

    private BitmapRegionLoader(InputStream in) {
        mInputStream = in;
        try {
            mBitmapRegionDecoder = BitmapRegionDecoder.newInstance(in, false);
            mOriginalWidth = mBitmapRegionDecoder.getWidth();
            mOriginalHeight = mBitmapRegionDecoder.getHeight();
            mValid = true;
        } catch (IOException e) {
            Log.e("GifCastTag", "Couldn't create bitmap loader.", e);
        }
    }

    /**
     * Key difference from
     * {@link BitmapRegionDecoder#decodeRegion(Rect, Options)} in this implementation is that even
     * if <code>inBitmap</code> is given, a sub-bitmap might be returned.
     */
    public synchronized Bitmap decodeRegion(Rect rect, Options options) {
        int unsampledInBitmapWidth = -1;
        int unsampledInBitmapHeight = -1;

        int sampleSize = Math.max(1, options != null ? options.inSampleSize : 1);
        if (options != null && options.inBitmap != null) {
            unsampledInBitmapWidth = options.inBitmap.getWidth() * sampleSize;
            unsampledInBitmapHeight = options.inBitmap.getHeight() * sampleSize;
        }

        mTempRect.set(rect);

        Bitmap bitmap = mBitmapRegionDecoder.decodeRegion(mTempRect, options);
        if (options != null && options.inBitmap != null && ((mTempRect.width() != unsampledInBitmapWidth || mTempRect.height() != unsampledInBitmapHeight))) {
            // Need to extract the sub-bitmap
            Bitmap subBitmap = Bitmap.createBitmap(bitmap, 0, 0, mTempRect.width() / sampleSize, mTempRect.height() / sampleSize);
            if (bitmap != options.inBitmap) {
                bitmap.recycle();
            }
            bitmap = subBitmap;
        }

        return bitmap;
    }

    public synchronized int getWidth() {
        return mOriginalWidth;
    }

    public synchronized int getHeight() {
        return mOriginalHeight;
    }

    public synchronized void destroy() {
        mBitmapRegionDecoder.recycle();
        mBitmapRegionDecoder = null;
        try {
            mInputStream.close();
        } catch (IOException ignored) {
        }
    }
}
