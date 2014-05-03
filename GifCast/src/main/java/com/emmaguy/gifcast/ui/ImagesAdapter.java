package com.emmaguy.gifcast.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.emmaguy.gifcast.R;
import com.emmaguy.gifcast.data.DrawableRequestQueue;
import com.emmaguy.gifcast.data.Image;

import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends BaseAdapter implements Filterable {
    private final DrawableRequestQueue mRequestQueue;
    private final LayoutInflater mViewInflater;
    private final RedditImagesFilter mFilter;

    private OnFilteringComplete mFilteringListener;

    private final List<Image> mOriginalImages = new ArrayList<Image>();
    private List<Image> mFilteredImages = new ArrayList<Image>();

    public ImagesAdapter(Context context, DrawableRequestQueue requestQueue, boolean hideNSFW) {
        mFilter = new RedditImagesFilter(hideNSFW);
        mRequestQueue = requestQueue;
        mViewInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setFilteringCompleteListener(OnFilteringComplete listener) {
        mFilteringListener = listener;
    }

    public void addImages(List<Image> images) {
        mOriginalImages.addAll(images);
        mFilteredImages = images;

        mFilter.filter("");
    }

    @Override
    public int getCount() {
        return mFilteredImages.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilteredImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // update the Image object for this cell
        final Image image = mFilteredImages.get(position);
        final ViewHolder viewHolder;
        if (view == null) {
            view = mViewInflater.inflate(R.layout.image_item, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageview);
            ;
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();

            if (viewHolder.imageView.getTag() != null) {
                if (image.hasUrl()) {
                    String oldUrl = viewHolder.imageView.getTag().toString();
                    if (!oldUrl.equals(image.thumbnailUrl())) {
                        mRequestQueue.cancelRequest(oldUrl);
                        viewHolder.imageView.setTag(null);
                        Log.d("GifCastTag", "reset tag index: " + position + ", removing tag url: " + oldUrl + " for: " + image.thumbnailUrl());
                    }
                }
            }
            Log.d("GifCastTag", "resetting index: " + position);
            viewHolder.imageView.setImageResource(R.drawable.animated_progress);
        }

        if (image.hasThumbnail()) {
            boolean hasImage = true;
            if(image.hasUrl()) {
                String firstUrl = image.getImageUrls()[0];
                if(mRequestQueue.hasImageForUrl(firstUrl)){
                    setDrawableOrRequest(firstUrl, viewHolder.imageView, position);
                } else {
                    hasImage = false;
                }
            } else {
                hasImage = false;
            }

            if(!hasImage) {
                setDrawableOrRequest(image.thumbnailUrl(), viewHolder.imageView, position);
            }
        } else if (image.hasUrl()) {
            // add a request for the full image too
            String[] urls = image.getImageUrls();

            setDrawableOrRequest(urls[0], viewHolder.imageView, position);
        }

        Log.d("GifCastTag", "exiting view position: " + position + " with tag " + viewHolder.imageView.getTag());
        return view;
    }

    private void setDrawableOrRequest(String url, ImageView imageView, int position) {
        Log.d("GifCastTag", "requesting index: " + position + ", for tag url: " + url);
        imageView.setTag(url);
        mRequestQueue.setDrawableOrAddRequest(url, imageView);
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

            if (mHideNSFW) {
                List<Image> filteredImages = new ArrayList<Image>();
                for (Image i : mOriginalImages) {
                    if (!i.isNSFW()) {
                        filteredImages.add(i);
                    }
                }
                filterResults.values = filteredImages;
            } else {
                filterResults.values = mOriginalImages;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mFilteredImages = (List<Image>) filterResults.values;
            if (mFilteringListener != null) {
                mFilteringListener.onFilteringComplete();
            }
            notifyDataSetChanged();
        }

        public void setFilterNSFW(boolean filterNSFW) {
            this.mHideNSFW = filterNSFW;
        }
    }

    private class ViewHolder {
        public ImageView imageView;
    }

    public interface OnFilteringComplete {
        void onFilteringComplete();
    }
}