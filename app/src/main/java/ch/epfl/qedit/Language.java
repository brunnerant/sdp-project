package ch.epfl.qedit;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class Language extends Application {

    private static String language;

    @Override
    public void onCreate() {
        language = "en";
        super.onCreate();
    }

    /**
     * Set the language.
     * @param context context in which the language must be changed.
     * @param languageCode language code corresponding to the wanted language
     */
    public static void setLanguage(Context context, String languageCode){
        language = languageCode;
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getApplicationContext().getResources().updateConfiguration(config,
                context.getApplicationContext().getResources().getDisplayMetrics());
    }

    /**
     * Get the language.
     * @return language code
     */
    public static String getLanguage() {
        return language;
    }

}