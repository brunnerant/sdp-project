package ch.epfl.qedit.util;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import ch.epfl.qedit.R;
import java.util.Arrays;
import java.util.Locale;

public class Utils {

    public static String getCurrentLanguageCode() {
        return Locale.getDefault().getLanguage();
    }

    public static int languagePositionInList(Resources resources, String languageCode) {
        String[] languageList = resources.getStringArray(R.array.languages_codes);
        return Arrays.asList(languageList).indexOf(languageCode);
    }

    /**
     * Display a toast to inform the user that the language was successfully changed
     *
     * @param languagePos position of the language in the spinner
     */
    public static void showToastChangedLanguage(
            int languagePos, int toastType, Context context, Resources resources) {
        String stringToDisplay =
                resources.getString(R.string.language_changed)
                        + " "
                        + resources.getStringArray(R.array.languages_list)[languagePos];

        showToast(stringToDisplay, toastType, context);
    }

    public static void showToast(String stringToDisplay, int toastType, Context context) {
        Toast.makeText(context, stringToDisplay, toastType).show();
    }

    public static void showToast(
            int stringId, int toastType, Context context, Resources resources) {
        showToast(resources.getString(stringId), toastType, context);
    }

    /**
     * Source: https://stackoverflow.com/a/17789187/13249857
     *
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from
        // it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String regexEmail() {
        // Source: regex n°2 on https://howtodoinjava.com/regex/java-regex-validate-email-address/
        return "^[A-Za-z0-9+_.-]+@(.+)$";
    }

    public static void putStringInPrefs(Activity activity, String key, String value) {
        SharedPreferences prefs = activity.getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getStringInPrefs(Activity activity, String key) {
        SharedPreferences prefs = activity.getSharedPreferences("UserData", MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    public static void removeStringInPrefs(Activity activity, String key) {
        SharedPreferences prefs = activity.getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.commit();
    }
}
