package ch.epfl.qedit.view.login;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.AuthenticationService;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.util.LocaleHelper;
import java.util.function.Predicate;

public class SignUpActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private EditText firstNameField,
            lastNameField,
            emailField,
            passwordField,
            passwordConfirmationField;
    private Button signUpButton;
    private ProgressBar progressBar;
    private TextView textViewLogInInstead;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    private AuthenticationService auth;

    private boolean userHasInteracted = false;

    private Context context;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = AuthenticationFactory.getInstance();

        context = getBaseContext();
        resources = getResources();

        initializeViews();

        signUpButton.setOnClickListener(v -> signUp());
        textViewLogInInstead.setOnClickListener(v -> logInInstead());

        Util.initializeLanguage(this, this);
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

    private void initializeViews() {
        firstNameField = findViewById(R.id.field_first_name);
        lastNameField = findViewById(R.id.field_last_name);
        emailField = findViewById(R.id.field_email);
        passwordField = findViewById(R.id.field_password);
        passwordConfirmationField = findViewById(R.id.field_password_confirmation);
        signUpButton = findViewById(R.id.button_sign_up);
        progressBar = findViewById(R.id.progress_bar);
        textViewLogInInstead = findViewById(R.id.log_in_instead);
        Util.updateRedirectionText(resources, textViewLogInInstead, R.string.log_in_instead);
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
        firstNameField.setHint(resources.getString(R.string.hint_first_name));
        lastNameField.setHint(resources.getString(R.string.hint_last_name));
        emailField.setHint(resources.getString(R.string.hint_email));
        passwordField.setHint(resources.getString(R.string.hint_password));
        passwordConfirmationField.setHint(resources.getString(R.string.hint_password_confirmation));
        signUpButton.setText(resources.getString(R.string.sign_up_button_text));
        Util.updateRedirectionText(resources, textViewLogInInstead, R.string.log_in_instead);

        setTitle(resources.getString(R.string.title_activity_sign_up));
    }

    /**
     * Check the validity of the values entered in the fields.
     *
     * @return whether or not all the inputs are valid
     */
    private boolean checkInputValidity() {
        // Check validity of all input
        Predicate<String> emailFormat = str -> str.matches(Util.REGEX_EMAIL);
        Predicate<String> passwordFormat = str -> str.length() >= 6;
        Predicate<String> nameFormat = str -> str.matches(Util.REGEX_NAME);
        email = Util.getString(emailField, emailFormat, resources, R.string.invalid_email);
        password =
                Util.getString(passwordField, passwordFormat, resources, R.string.invalid_password);
        firstName = Util.getString(firstNameField, nameFormat, resources, R.string.invalid_name);
        lastName = Util.getString(lastNameField, nameFormat, resources, R.string.invalid_name);

        // Check if password confirmation match actual password
        if (password != null) {
            Predicate<String> matchPsw = str -> password.equals(str);
            String confirmation =
                    Util.getString(
                            passwordConfirmationField,
                            matchPsw,
                            resources,
                            R.string.invalid_password_confirmation);
            if (confirmation == null) return false;
        }

        return email != null && password != null && firstName != null && lastName != null;
    }

    /** Sign up the user. */
    private void signUp() {

        if (!checkInputValidity()) return;

        // Remove leading and trailing space of email, first and last name
        email = email.trim();
        firstName = firstName.trim();
        lastName = lastName.trim();

        progressBar.setVisibility(View.VISIBLE);

        auth.signUp(email, password)
                .whenComplete(
                        (userId, error) -> {
                            if (error != null) onSignUpFail();
                            else onSignUpSuccessful(userId);
                        });
    }

    private void onSignUpFail() {
        progressBar.setVisibility(View.GONE);
        Util.showToast(R.string.sign_up_fail, context, resources);
    }

    private void onSignUpSuccessful(String userId) {
        Intent intent = new Intent(this, LogInActivity.class);

        DatabaseService db = DatabaseFactory.getInstance();
        db.createUser(userId, firstName, lastName)
                .whenComplete(
                        (result, throwable) -> {
                            progressBar.setVisibility(View.GONE);
                            if (throwable != null) {
                                Util.showToast(R.string.database_error, context, resources);
                            } else {
                                startActivity(intent);
                            }
                        });
        // Put the current user id in cache
        Util.putStringInPrefs(this, Util.USER_ID, userId);

        Util.showToast(R.string.sign_up_success, context, resources);
    }

    private void logInInstead() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
