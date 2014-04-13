package com.emmaguy.gifcast.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.emmaguy.gifcast.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.nio.ByteBuffer;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class ImageDetailActivity extends Activity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_detail);

        linearLayout = (LinearLayout)findViewById(R.id.linearlayout);

        Bundle extras = getIntent().getExtras();
        String[] urls = extras.getStringArray("url");

        for(int i = urls.length - 1; i >= 0; i--) {

            final GifImageView imageView = new GifImageView(this);

            com.squareup.picasso.Target t = new com.squareup.picasso.Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    int bytes = bitmap.getByteCount();
                    ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
                    bitmap.copyPixelsToBuffer(buffer); //Move the byte data to the buffer

                    try {
                        GifDrawable gifFromBytes = new GifDrawable(buffer.array());
                        imageView.setImageDrawable(gifFromBytes);

                    } catch (IOException e) {
                        Log.e("Emma", "Exception making gif: " + e.getMessage(), e);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    Log.e("Emma", "Exception onBitmapFailed: " + errorDrawable);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);

            linearLayout.addView(imageView, 0, params);

            

            Picasso.with(this).load(urls[i]).into(t);
        }
    }
}
