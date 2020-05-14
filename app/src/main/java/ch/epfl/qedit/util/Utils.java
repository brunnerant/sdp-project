package ch.epfl.qedit.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import ch.epfl.qedit.R;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;

public class Utils {

    // Source: regex n°2 on https://howtodoinjava.com/regex/java-regex-validate-email-address/
    public static final String REGEX_EMAIL = "^(\\s)*[A-Za-z0-9+_.-]+@(.+)(\\s)*$";
    public static final String REGEX_NAME = "^(\\s)*[A-Za-z0-9+-\\s]+(\\s)*$";

    /**
     * Check if the string written in the text view is not empty and respect a certain predicate. If
     * text is not valid, set an error to the TextView
     *
     * @param view text view to check
     * @param predicate predicate that must be valid about the string we check
     * @param resources in order to extract the correct error message in the correct language
     * @param errorString the id of the custom error message if the predicate is false
     * @return the text of the text view or null if the text is not valid
     */
    public static String checkString(
            EditText view, Predicate<String> predicate, Resources resources, int errorString) {
        String text = view.getText().toString();
        if (TextUtils.isEmpty(text)) {
            view.setError(resources.getString(R.string.input_cannot_be_empty));
            text = null;
        } else if (!predicate.test(text)) {
            view.setError(resources.getString(errorString));
            text = null;
        }
        return text;
    }

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
}
