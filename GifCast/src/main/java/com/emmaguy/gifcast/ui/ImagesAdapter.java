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

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends BaseAdapter {

    private final LayoutInflater mViewInflater;
    private final CachedRequestQueue mRequestQueue;

    private final List<Image> images = new ArrayList<Image>();

    public ImagesAdapter(Context context, CachedRequestQueue requestQueue) {
        mRequestQueue = requestQueue;
        mViewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addImageUrls(List<Image> images) {
        this.images.addAll(images);

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
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
            viewHolder.imageView.setImageDrawable(null);
            viewHolder.textView.setText(null);
            viewHolder.title.setText(null);
        }

        // update the Image object for this cell
        Image image = images.get(position);

        if(image.getNumberOfImages() > 0) {
            viewHolder.imageView.setTag(image.thumbnailUrl());
            viewHolder.textView.setText(image.getNumberOfImages() + " " + image.thumbnailUrl());
            viewHolder.title.setText(image.getTitle());
            //mRequestQueue.addRequest(image.thumbnailUrl(), viewHolder.imageView);
        }

        return view;
    }

    private class ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView title;
    }
}