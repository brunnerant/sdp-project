package ch.epfl.qedit.view.login;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ch.epfl.qedit.R;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Predicate;

/** A Utility class useful for login and sign up */
public final class Util {

    static final String REGEX_EMAIL = "^(\\s)*[A-Za-z0-9+_.-]+@(.+)(\\s)*$";
    static final String REGEX_NAME = "^(\\s)*[A-Za-z0-9\\s\\-]+(\\s)*$";

    // This is the name of the shared preference file, and the key for the user id
    public static final String USER_DATA = "user_data";
    public static final String USER_ID = "user_id";

    private Util() {}

    /**
     * Check if the string written in the text view is not empty and respect a certain predicate. If
     * text is not valid, set an error to the TextView and return null.
     *
     * @param view text view to check
     * @param predicate predicate that must be valid about the string we check
     * @param resources in order to extract the correct error message in the correct language
     * @param errorString the id of the custom error message if the predicate is false
     * @return the text of the text view or null if the text is not valid
     */
    static String getString(
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

    /**
     * Get the language code of the actual language of the app.
     *
     * @return the language code
     */
    private static String getCurrentLanguageCode() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * Compute the position of the given language in the languages list.
     *
     * @param resources resources to get the strings from
     * @param languageCode language code of the language from which we want to retrieve the position
     * @return the language position
     */
    private static int languagePositionInList(Resources resources, String languageCode) {
        String[] languageList = resources.getStringArray(R.array.languages_codes);
        return Arrays.asList(languageList).indexOf(languageCode);
    }

    /**
     * Initialize the language selection spinner and the language of the activity.
     *
     * @param activity the activity in which the language selection spinner is
     * @param onItemSelectedListener listener called when an item of the spinner is selected
     */
    public static void initializeLanguage(
            Activity activity, AdapterView.OnItemSelectedListener onItemSelectedListener) {
        // Create spinner (language list)
        Spinner languageSelectionSpinner = activity.findViewById(R.id.spinner_language_selection);
        // Find app's current language position in languages list
        int positionInLanguageList =
                Util.languagePositionInList(activity.getResources(), Util.getCurrentLanguageCode());
        // Set current language in spinner at startup
        languageSelectionSpinner.setSelection(positionInLanguageList, false);
        // Set listener
        languageSelectionSpinner.setOnItemSelectedListener(onItemSelectedListener);
    }

    /**
     * Update the redirection text with colors (we cannot directly put the color in the strings.xml
     * because we are changing the language dynamically in these activities).
     *
     * @param resources resources to get the strings from
     * @param view TextView to update
     * @param stringId id of the string tu put in the TextView
     */
    public static void updateRedirectionText(Resources resources, TextView view, int stringId) {
        int color = resources.getColor(R.color.colorRedirection);
        view.setText(resources.getString(stringId));
        view.setTextColor(color);
    }

    /**
     * Display a toast to inform the user that the language was successfully changed.
     *
     * @param languagePos position of the language in the spinner
     * @param context context containing the current app's language
     * @param resources resources to get the strings from
     */
    static void showToastChangedLanguage(int languagePos, Context context, Resources resources) {
        String stringToDisplay =
                resources.getString(R.string.language_changed)
                        + " "
                        + resources.getStringArray(R.array.languages_list)[languagePos];

        showToast(stringToDisplay, context);
    }

    /**
     * Given a string, display a short toast.
     *
     * @param stringToDisplay string to display in the toast
     * @param context context containing the current app's language
     */
    private static void showToast(String stringToDisplay, Context context) {
        Toast.makeText(context, stringToDisplay, Toast.LENGTH_SHORT).show();
    }

    /**
     * Given a string id, display a short toast.
     *
     * @param stringId id of the string to display in the toast
     * @param context context containing the current app's language
     * @param resources resources to get the string from
     */
    public static void showToast(int stringId, Context context, Resources resources) {
        showToast(resources.getString(stringId), context);
    }

    /**
     * Put a string in the preferences.
     *
     * @param activity current activity
     * @param key key to put
     * @param value value to put with the key
     */
    @SuppressWarnings("SameParameterValue")
    static void putStringInPrefs(Activity activity, String key, String value) {
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
