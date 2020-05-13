package ch.epfl.qedit.view.login;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ch.epfl.qedit.R;
import ch.epfl.qedit.util.Utils;
import ch.epfl.qedit.util.LocaleHelper;

public class LogInActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private EditText emailField, passwordField;
    private Button logInButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private boolean userHasInteracted = false;

    private Context context;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();

        logInButton.setOnClickListener(v -> logIn());

        context = getBaseContext();
        resources = getResources();

        initializeLanguage();
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
        Utils.printChangedLanguageToast(pos, Toast.LENGTH_SHORT, getApplicationContext(), resources);
    }

    @Override
    public void onNothingSelected(AdapterView parent) {
        // Not used because there will always be something selected
    }

    private void initializeViews() {
        emailField = findViewById(R.id.field_email);
        passwordField = findViewById(R.id.field_password);
        logInButton = findViewById(R.id.button_log_in);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initializeLanguage() {
        // Create spinner (language list)
        Spinner languageSelectionSpinner = findViewById(R.id.spinner_language_selection);
        // Find app's current language position in languages list
        int positionInLanguageList = Utils.languagePositionInList(resources, Utils.getCurrentLanguageCode());
        // Set current language in spinner at startup
        languageSelectionSpinner.setSelection(positionInLanguageList, false);
        // Set listener
        languageSelectionSpinner.setOnItemSelectedListener(this);
        // Set page title to display it in the right language
        setTitle(R.string.title_activity_log_in);
    }

    /**
     * Set the new language
     *
     * @param languageCode the universal language code (e.g. "en" for English, "fr" for French)
     */
    private void setLanguage(String languageCode) {
        context = LocaleHelper.setLocale(this, languageCode);
        resources = context.getResources();
    }

    /** Update activity's texts */
    private void updateTexts() {
        emailField.setHint(resources.getString(R.string.hint_email));
        passwordField.setHint(resources.getString(R.string.hint_password));
        logInButton.setText(resources.getString(R.string.log_in_button_text));
        setTitle(resources.getString(R.string.title_activity_log_in));
    }

    private void logIn() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "email empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "password empty", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                onLogInSuccessful();
                            } else {
                                onLogInFailed();
                            }
                        });
    }

    private void onLogInSuccessful() {
        Utils.showToast(R.string.log_in_success, Toast.LENGTH_SHORT, getApplicationContext(), resources);
        progressBar.setVisibility(View.GONE);

        Intent intent =
                new Intent(LogInActivity.this, TokenLogInActivity.class);
        // TODO bundle user
        startActivity(intent);
    }

    private void onLogInFailed() {
        Utils.showToast(R.string.log_in_fail, Toast.LENGTH_SHORT, getApplicationContext(), resources);
        progressBar.setVisibility(View.GONE);
    }
}
