package ch.epfl.qedit.view.login;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.AuthenticationService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Utils;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.view.home.HomeActivity;

public class TokenLogInActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    public static final String USER = "ch.epfl.qedit.view.USER";

    private EditText tokenText;
    private Button logInButton;
    private ProgressBar progressBar;

    private AuthenticationService authService;
    private Handler handler;

    private boolean userHasInteracted = false;

    private Context context;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token_log_in);

        authService = AuthenticationFactory.getInstance();
        handler = new Handler();

        initializeViews();

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
        tokenText = findViewById(R.id.field_token);
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
        setTitle(R.string.title_activity_token_log_in);
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
        tokenText.setHint(resources.getString(R.string.hint_token));
        logInButton.setText(resources.getString(R.string.log_in_button_text));
        setTitle(resources.getString(R.string.title_activity_token_log_in));
    }

    public void handleLogIn(View view) {
        Utils.hideKeyboard(this);
        String token = tokenText.getText().toString();
        // Sanitize token
        if(TextUtils.isEmpty(token)) {
            tokenText.setError(resources.getString(R.string.input_cannot_be_empty));
            return;
        }
        token = token.trim(); // Remove leading and trailing spaces in the token

        // This regular expression will accept only strings of length 20
        // and composed of letters and digits
        if (!token.matches("[a-zA-Z0-9]{20}")) {
            tokenText.setError(resources.getString(R.string.invalid_token));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        authService.sendRequest(
                token,
                response -> handler.post(
                        () -> {
                            progressBar.setVisibility(View.GONE);
                            if (response.getError().noError(context)) {
                                onLogInSuccessful(response.getData());
                            }
                        }));
    }

    private void onLogInSuccessful(User user) {
        Intent intent = new Intent(TokenLogInActivity.this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
