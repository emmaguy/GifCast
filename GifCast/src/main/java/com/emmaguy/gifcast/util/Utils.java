package com.emmaguy.gifcast.util;

import android.app.Activity;

import com.emmaguy.gifcast.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class Utils {
    public static boolean isGif(String pictureFileName) {
        return pictureFileName.endsWith(".gif");
    }

    public static boolean isImage(String pictureFileName) {
        return pictureFileName.endsWith(".png")
                || isGif(pictureFileName)
                || pictureFileName.endsWith(".jpg")
                || pictureFileName.endsWith(".jpeg");
    }

    public static void tintActionBar(Activity a) {
        SystemBarTintManager tintManager = new SystemBarTintManager(a);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintColor(a.getResources().getColor(R.color.hot_pink));
    }
}