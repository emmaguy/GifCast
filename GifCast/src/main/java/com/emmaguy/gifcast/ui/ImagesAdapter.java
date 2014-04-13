package com.emmaguy.gifcast.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmaguy.gifcast.CachedRequestQueue;
import com.emmaguy.gifcast.R;

import java.util.Collections;
import java.util.List;

public class ImagesAdapter extends BaseAdapter {

    private final LayoutInflater mViewInflater;
    private final Context mContext;
    private final CachedRequestQueue mRequestQueue;

    private List<Image> images = Collections.emptyList();

    public ImagesAdapter(Context context, CachedRequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        mContext = context;
        mViewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setImageUrls(List<Image> images) {
        this.images = images;

        notifyDataSetChanged();
    }

    @Override public int getCount() {
        return images.size();
    }

    @Override public Object getItem(int position) {
        return images.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = mViewInflater.inflate(R.layout.image_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageview);;
            viewHolder.textView = (TextView) view.findViewById(R.id.textview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
            viewHolder.imageView.setImageDrawable(null);
            viewHolder.textView.setText(null);

            // cancel the request for the old cell
//            if(viewHolder.image.getNumberOfImages() > 0) {
//                mRequestQueue.cancelRequest(viewHolder.image.thumbnailUrl());
//            }
        }

        // update the Image object for this cell
        viewHolder.image = images.get(position);

        if(viewHolder.image.getNumberOfImages() > 0) {
            viewHolder.textView.setText(viewHolder.image.getNumberOfImages() + " " + viewHolder.image.thumbnailUrl());
            mRequestQueue.addRequest(viewHolder.image.thumbnailUrl(), viewHolder.imageView);
        }

        return view;
    }

    private class ViewHolder {
        public Image image;
        public ImageView imageView;
        public TextView textView;
    }
}