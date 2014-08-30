package com.emmaguy.gifcast.data;

import java.util.List;

public interface OnRedditItemsChanged {
    void onNewItemsAdded(List<Image> images);
    void onItemsChanged();
}
