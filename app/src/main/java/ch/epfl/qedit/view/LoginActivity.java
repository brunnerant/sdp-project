package ch.epfl.qedit.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.AuthenticationService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.util.Response;
import ch.epfl.qedit.view.home.HomeActivity;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String USER = "ch.epfl.qedit.view.USER";

    private EditText tokenText;
    private Button loginButton;
    private ProgressBar progressBar;

    private AuthenticationService authService;
    private Handler handler;

    private boolean userHasInteracted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tokenText = findViewById(R.id.login_token);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.login_progress_bar);

        authService = AuthenticationFactory.getInstance();
        handler = new Handler();

        /** Language selection */
        // Create spinner (language list)
        Spinner languageSelectionSpinner = (Spinner) findViewById(R.id.language_selection);

        // Find app's current language position in languages list
        String currentLanguage = Locale.getDefault().getLanguage();
        int positionInLanguageList = 0;
        String[] languageList = getResources().getStringArray(R.array.languages_codes);
        boolean languageFound = false;
        for (int i = 0; i < languageList.length && !languageFound; ++i) {
            if (currentLanguage.equals(languageList[i])) {
                positionInLanguageList = i;
                languageFound = true;
            }
        }

        // Set current language in spinner at startup
        languageSelectionSpinner.setSelection(positionInLanguageList, false);
        // Set listener
        languageSelectionSpinner.setOnItemSelectedListener(this);
        // Set page title to display it in the right language
        setTitle(R.string.title_activity_login);
    }

    @Override
    /** This method is needed to apply the desired language at the activity startup */
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    /** This method tells us if the user has interacted with the activity since it was started */
    public void onUserInteraction() {
        super.onUserInteraction();
        userHasInteracted = true;
    }

    @Override
    /** This method runs if the user selects another language */
    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
        // Do not run if user has not chosen a language
        if (!userHasInteracted) {
            return;
        }

        // Get language code from the position of the clicked language in the spinner
        String languageCode = getResources().getStringArray(R.array.languages_codes)[pos];

        updateTextsAndMakeToast(languageCode, pos);
    }

    @Override
    public void onNothingSelected(AdapterView parent) {
        // Not used because there will always be something selected
    }

    /**
     * Set new language, update activity's texts and display a toast to inform the user
     *
     * @param languageCode the universal language code (e.g. "en" for English, "fr" for French)
     * @param languagePos position of the language in the spinner
     */
    private void updateTextsAndMakeToast(String languageCode, int languagePos) {
        // Set new language
        Context context = LocaleHelper.setLocale(this, languageCode);

        // Update texts
        Resources resources = context.getResources();
        tokenText.setHint(resources.getString(R.string.token_hint));
        loginButton.setText(resources.getString(R.string.login_button_text));
        setTitle(resources.getString(R.string.title_activity_login));

        // Display changed language confirmation
        Toast.makeText(
                getApplicationContext(),
                resources.getString(R.string.language_changed)
                        + " "
                        + resources.getStringArray(R.array.languages_list)[languagePos],
                Toast.LENGTH_SHORT)
                .show();
    }

    public void handleLogin(View view) {
        String token = tokenText.getText().toString();
        // Sanitize token
        if (token.isEmpty()) {
            printShortToast(R.string.empty_token_message);
            return;
        }
        token = token.trim(); // Remove leading and trailing spaces in the token

        // This regular expression will accept only strings of length 20
        // and composed of letters and digits
        if (!token.matches("[a-zA-Z0-9]{20}")) {
            printShortToast(R.string.wrong_token_message);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        authService.sendRequest(
                token,
                new Callback<Response<User>>() {
                    @Override
                    public void onReceive(final Response<User> response) {
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        if (response.successful())
                                            onLoginSuccessful(response.getData());
                                        else onLoginFailed(response.getError());
                                    }
                                });
                    }
                });
    }

    private void onLoginSuccessful(User user) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void onLoginFailed(int error) {
        int stringId = 0;
        switch (error) {
            case AuthenticationService.CONNECTION_ERROR:
                stringId = R.string.connection_error_message;
                break;
            case AuthenticationService.WRONG_TOKEN:
                stringId = R.string.wrong_token_message;
                break;
            default:
                break;
        }

        printShortToast(stringId);
    }

    private void printShortToast(int stringId) {
        Toast toast =
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(stringId),
                        Toast.LENGTH_SHORT);
        toast.show();
    }
}
