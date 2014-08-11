package com.emmaguy.gifcast.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.emmaguy.gifcast.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
    private static Set<String> DefaultSubReddits = new HashSet<String>(Arrays.asList("babyelephantgifs","pics", "gifs", "spaceporn", "earthporn", "itookapicture", "exposureporn", "highres"));

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

    public static List<String> allSubReddits(Context a) {
        SharedPreferences prefs = a.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        Set<String> subReddits = prefs.getStringSet("subreddits", DefaultSubReddits);

        ArrayList<String> sortedSubreddits = new ArrayList(subReddits);
        Collections.sort(sortedSubreddits);

        return sortedSubreddits;
    }

    public static List<String> selectedSubReddits(Context a) {
        SharedPreferences prefs = a.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        Set<String> subReddits = prefs.getStringSet("selectedsubreddits", DefaultSubReddits);

        return new ArrayList(subReddits);
    }

    public static void saveSubreddits(Context c, List<String> srs) {
        SharedPreferences prefs = c.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        prefs.edit().putStringSet("subreddits", new HashSet<String>(srs)).apply();
    }

    public static void saveSelectedSubreddits(Context c, List<String> selectedSubreddits) {
        SharedPreferences prefs = c.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        prefs.edit().putStringSet("selectedsubreddits", new HashSet<String>(selectedSubreddits)).apply();
    }

    public static void addSubreddit(Context c, String subreddit) {
        List<String> subreddits = allSubReddits(c);
        subreddits.add(subreddit);

        saveSubreddits(c, subreddits);
    }
}