package ch.epfl.qedit;

import android.app.Application;
import android.content.Context;
import ch.epfl.qedit.util.LocaleHelper;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
