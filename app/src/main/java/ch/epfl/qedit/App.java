package ch.epfl.qedit;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import ch.epfl.qedit.LocaleHelper;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }
}
