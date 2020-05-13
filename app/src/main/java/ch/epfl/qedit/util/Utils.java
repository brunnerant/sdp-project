package ch.epfl.qedit.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Locale;

import ch.epfl.qedit.R;

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
    public static void printChangedLanguageToast(int languagePos, int toastType, Context context, Resources resources) {
        String stringToDisplay = resources.getString(R.string.language_changed)
                + " "
                + resources.getStringArray(R.array.languages_list)[languagePos];

        showToast(stringToDisplay, toastType, context);
    }

    public static void showToast(String stringToDisplay, int toastType, Context context) {
        Toast.makeText(context, stringToDisplay, toastType).show();
    }

    public static void showToast(int stringId, int toastType, Context context, Resources resources) {
        showToast(resources.getString(stringId), toastType, context);
    }

    /**
     * Source: https://stackoverflow.com/a/17789187/13249857
     * @param activity
     */
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
