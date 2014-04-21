package com.emmaguy.gifcast.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmaguy.gifcast.CachedRequestQueue;
import com.emmaguy.gifcast.R;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends BaseAdapter implements Filterable {

    private final LayoutInflater mViewInflater;
    private final CachedRequestQueue mRequestQueue;
    private final RedditImagesFilter mFilter;

    private final List<Image> mOriginalImages = new ArrayList<Image>();
    private List<Image> mFilteredImages = new ArrayList<Image>();

    public ImagesAdapter(Context context, CachedRequestQueue requestQueue, boolean hideNSFW) {
        mFilter = new RedditImagesFilter(hideNSFW);
        mRequestQueue = requestQueue;
        mViewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addImageUrls(List<Image> images) {
        this.mOriginalImages.addAll(images);

        mFilter.filter("");
    }

    @Override public int getCount() {
        return mFilteredImages.size();
    }

    @Override public Object getItem(int position) {
        return mFilteredImages.get(position);
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

            if(viewHolder.imageView.getTag() != null) {
                String oldUrl = viewHolder.imageView.getTag().toString();
                mRequestQueue.cancelRequest(oldUrl);

                viewHolder.imageView.setTag(null);
            }

            viewHolder.imageView.setImageDrawable(null);
            viewHolder.textView.setText(null);
            viewHolder.title.setText(null);
        }

        // update the Image object for this cell
        Image image = mFilteredImages.get(position);

        if(image.getNumberOfImages() > 0) {
            viewHolder.imageView.setTag(image.thumbnailUrl());
            viewHolder.textView.setText(image.getNumberOfImages() + " " + image.thumbnailUrl());
            viewHolder.title.setText(image.getTitle());

            mRequestQueue.addRequest(image.thumbnailUrl(), viewHolder.imageView);
        }

        return view;
    }

    public void toggleNSFWFilter(boolean hideNSFW) {
        mFilter.setFilterNSFW(hideNSFW);
        mFilter.filter("");
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class RedditImagesFilter extends Filter {

        private boolean mHideNSFW;

        public RedditImagesFilter(boolean hideNSFW) {
            mHideNSFW = hideNSFW;
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();

            if(mHideNSFW) {
                List<Image> filteredImages = new ArrayList<Image>();
                for(Image i : mOriginalImages) {
                    if(!i.isNSFW()) {
                        filteredImages.add(i);
                    }
                }
                filterResults.values = filteredImages;
            }
            else {
                filterResults.values = mOriginalImages;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mFilteredImages = (List<Image>) filterResults.values;
            notifyDataSetChanged();
        }

        public void setFilterNSFW(boolean filterNSFW) {
            this.mHideNSFW = filterNSFW;
        }
    }

    private class ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView title;
    }
}