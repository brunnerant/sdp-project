package ch.epfl.qedit;

import android.app.Application;
import android.content.Context;
import ch.epfl.qedit.util.LocaleHelper;

public class App extends Application {

    @Override
    /**
     * This method is needed to apply the desired language at the app startup. In this way, the
     * user's language choice will persist when the application is restarted.
     */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
