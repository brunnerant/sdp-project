package ch.epfl.qedit.view;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.StartActivity;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.login.Util;

public class SettingsActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private TextView textViewSettings;
    private Button saveButton;

    private boolean userHasInteracted = false;

    private Context context;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        context = getBaseContext();
        resources = getResources();

        initializeViews();

        saveButton.setOnClickListener(v -> save());

        Util.initializeLanguage(this, this);

        setTitle(resources.getString(R.string.title_activity_settings));
    }

    @Override
    /* This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    /* This method tells us if the user has interacted with the activity since it was started */
    public void onUserInteraction() {
        super.onUserInteraction();
        userHasInteracted = true;
    }

    @Override
    /* This method runs if the user selects another language */
    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
        // Do not run if user has not chosen a language
        if (!userHasInteracted) {
            return;
        }

        // Get language code from the position of the clicked language in the spinner
        String languageCode = resources.getStringArray(R.array.languages_codes)[pos];

        setLanguage(languageCode);
        updateTexts();
        Util.showToastChangedLanguage(pos, context, resources);
    }

    @Override
    public void onNothingSelected(AdapterView parent) {
        // Not used because there will always be something selected
    }

    private void save() {
        String userId = StartActivity.retrieveUserId();
        StartActivity.retrieveUserAndLaunchHomeActivity(this, userId);
    }

    private void initializeViews() {
        textViewSettings = findViewById(R.id.change_language_text);
        saveButton = findViewById(R.id.button_save);
    }

    /**
     * Change app's language.
     *
     * @param languageCode the universal language code (e.g. "en" for English, "fr" for French)
     */
    private void setLanguage(String languageCode) {
        context = LocaleHelper.setLocale(this, languageCode);
        resources = context.getResources();
    }

    /** Update activity's texts (useful when the language is changed). */
    private void updateTexts() {
        textViewSettings.setText(resources.getString(R.string.change_language));
        saveButton.setText(resources.getString(R.string.save_settings));
        setTitle(resources.getString(R.string.title_activity_settings));
    }
}
