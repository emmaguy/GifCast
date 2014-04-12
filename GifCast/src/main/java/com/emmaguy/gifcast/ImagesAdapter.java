package com.emmaguy.gifcast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class ImagesAdapter extends BaseAdapter {

    private final LayoutInflater mViewInflater;
    private final Context mContext;

    private List<String> mImageUrls = Collections.emptyList();

    public ImagesAdapter(Context context) {
        mContext = context;
        mViewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setImageUrls(List<String> urls) {
        mImageUrls = urls;

        notifyDataSetChanged();
    }

    @Override public int getCount() {
        return mImageUrls.size();
    }

    @Override public Object getItem(int position) {
        return mImageUrls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = mViewInflater.inflate(R.layout.image_item, null);
        }
        String url = mImageUrls.get(position);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        TextView textView = (TextView)view.findViewById(R.id.textview);
        textView.setText(url);

        Picasso.with(mContext).load(url).into(imageView);

        return view;
    }
}