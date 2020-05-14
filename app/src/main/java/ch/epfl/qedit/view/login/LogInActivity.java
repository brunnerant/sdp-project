package ch.epfl.qedit.view.login;

import static ch.epfl.qedit.util.Utils.checkString;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.AuthenticationService;
import ch.epfl.qedit.backend.database.FirebaseDBService;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.util.Utils;
import ch.epfl.qedit.view.home.HomeActivity;
import java.util.function.Predicate;

public class LogInActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String USER = "ch.epfl.qedit.view.USER";

    private EditText emailField, passwordField;
    private Button logInButton;
    private ProgressBar progressBar;
    private TextView textViewSignUpInstead;

    private AuthenticationService auth;

    private boolean userHasInteracted = false;

    private Context context;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        auth = AuthenticationFactory.getInstance();

        context = getBaseContext();
        resources = getResources();

        initializeViews();

        logInButton.setOnClickListener(v -> logIn());
        textViewSignUpInstead.setOnClickListener(v -> signUpInstead());

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
        Utils.showToastChangedLanguage(pos, Toast.LENGTH_SHORT, context, resources);
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
        textViewSignUpInstead = findViewById(R.id.sign_up_instead);
        updateSignUpInsteadText();
    }

    private void initializeLanguage() {
        // Create spinner (language list)
        Spinner languageSelectionSpinner = findViewById(R.id.spinner_language_selection);
        // Find app's current language position in languages list
        int positionInLanguageList =
                Utils.languagePositionInList(resources, Utils.getCurrentLanguageCode());
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
        updateSignUpInsteadText();

        setTitle(resources.getString(R.string.title_activity_log_in));
    }

    private void updateSignUpInsteadText() {
        int colorNotSignedUpAlreadySignedUp =
                resources.getColor(R.color.colorNotSignedUpAlreadySignedUp);
        Spanned coloredText =
                Html.fromHtml(
                        "<font color='"
                                + colorNotSignedUpAlreadySignedUp
                                + "'>"
                                + resources.getString(R.string.sign_up_instead)
                                + "</font>");
        textViewSignUpInstead.setText(coloredText);
    }

    private void logIn() {

        Predicate<String> emailFormat = str -> str.matches(Utils.REGEX_EMAIL);
        Predicate<String> passwordFormat = str -> str.length() >= 6;
        String email = checkString(emailField, emailFormat, resources, R.string.invalid_email);
        String password =
                checkString(passwordField, passwordFormat, resources, R.string.invalid_password);

        if (email == null || password == null) return;

        email = email.trim();

        progressBar.setVisibility(View.VISIBLE);

        auth.logIn(email, password)
                .whenComplete(
                        (userId, error) -> {
                            if (error != null) onLogInFailed();
                            else onLogInSuccessful(userId);
                        });
    }

    private void onLogInSuccessful(String userId) {
        progressBar.setVisibility(View.GONE);

        Intent intent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();

        FirebaseDBService db = new FirebaseDBService();
        db.getUser(userId)
                .whenComplete(
                        (user, throwable) -> {
                            if (throwable != null) {
                                Utils.showToast(
                                        R.string.database_error, Toast.LENGTH_SHORT, context, resources);
                            } else {
                                bundle.putSerializable(USER, user);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
        // Put the current user id in cache
        Utils.putStringInPrefs(this, "user_id", userId);
    }

    private void onLogInFailed() {
        progressBar.setVisibility(View.GONE);
        Utils.showToast(
                R.string.log_in_fail, Toast.LENGTH_SHORT, context, resources);
    }

    private void signUpInstead() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}
