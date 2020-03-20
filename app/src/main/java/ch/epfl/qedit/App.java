package ch.epfl.qedit;

import android.app.Application;
import android.content.res.Configuration;

import java.util.Locale;

public class App extends Application {
    public void onCreate(){
        super.onCreate();

        LocaleUtils.setLocale(new Locale("en"));
        LocaleUtils.updateConfig(this, getBaseContext().getResources().getConfiguration());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.updateConfig(this, newConfig);
    }
}
