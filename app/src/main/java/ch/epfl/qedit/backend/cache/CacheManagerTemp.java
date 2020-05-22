package ch.epfl.qedit.backend.cache;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static ch.epfl.qedit.view.login.Util.USER_DATA;

public class CacheManagerTemp {



    //_____________________________________________________________________________

    /**
     * Put a string in the preferences.
     *
     * @param activity current activity
     * @param key key to put
     * @param value value to put with the key
     */
    public static void putStringInPrefs(Activity activity, String key, String value) {
        SharedPreferences prefs = activity.getSharedPreferences(USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get a string from a key in the preferences.
     *
     * @param activity current activity
     * @param key key associated with the value to retrieve
     * @return the string associated with the key in the preferences
     */
    public static String getStringInPrefs(Activity activity, String key) {
        SharedPreferences prefs = activity.getSharedPreferences(USER_DATA, MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    /**
     * Remove a string in the preferences.
     *
     * @param activity current activity
     * @param key key to remove
     */
    public static void removeStringInPrefs(Activity activity, String key) {
        SharedPreferences prefs = activity.getSharedPreferences(USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
    }
}
