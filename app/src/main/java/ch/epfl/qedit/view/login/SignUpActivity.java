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

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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
        firstNameField = findViewById(R.id.field_first_name);
        lastNameField = findViewById(R.id.field_last_name);
        emailField = findViewById(R.id.field_email);
        passwordField = findViewById(R.id.field_password);
        passwordConfirmationField = findViewById(R.id.field_password_confirmation);
        signUpButton = findViewById(R.id.button_sign_up);
        progressBar = findViewById(R.id.progress_bar);
        textViewLogInInstead = findViewById(R.id.sign_up_instead);
        updateLogInInsteadText();
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
        setTitle(R.string.title_activity_sign_up);
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
        firstNameField.setHint(resources.getString(R.string.hint_first_name));
        lastNameField.setHint(resources.getString(R.string.hint_last_name));
        emailField.setHint(resources.getString(R.string.hint_email));
        passwordField.setHint(resources.getString(R.string.hint_password));
        passwordConfirmationField.setHint(resources.getString(R.string.hint_password_confirmation));
        signUpButton.setText(resources.getString(R.string.sign_up_button_text));
        updateLogInInsteadText();

        setTitle(resources.getString(R.string.title_activity_sign_up));
    }

    private void updateLogInInsteadText() {
        int colorAlreadySignedUp =
                resources.getColor(R.color.colorNotSignedUpAlreadySignedUp);
        Spanned coloredText =
                Html.fromHtml(
                        "<font color='"
                                + colorAlreadySignedUp
                                + "'>"
                                + resources.getString(R.string.log_in_instead)
                                + "</font>");
        textViewLogInInstead.setText(coloredText);
    }

    private void confirmPassword() {
        String confirmation = passwordConfirmationField.getText().toString();
    }

    private boolean checkInputValidity() {
        // Check validity of all input
        Predicate<String> emailFormat = str -> str.matches(Utils.REGEX_EMAIL);
        Predicate<String> passwordFormat = str -> str.length() >= 6;
        Predicate<String> nameFormat = str -> str.matches(Utils.REGEX_NAME);
        email = checkString(emailField, emailFormat, resources, R.string.invalid_email);
        password =
                checkString(
                        passwordField, passwordFormat, resources, R.string.invalid_password);
        firstName = checkString(firstNameField, nameFormat, resources, R.string.invalid_name);
        lastName = checkString(lastNameField, nameFormat, resources, R.string.invalid_name);

        return email != null && password != null && firstName != null && lastName != null;
    }

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
        Toast.makeText(getApplicationContext(), "sign up fail", Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);
    }

    private void onSignUpSuccessful(String userId) {
        Toast.makeText(getApplicationContext(), "sign up success", Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.GONE);

        Intent intent = new Intent(this, HomeActivity.class);

        FirebaseDBService db = new FirebaseDBService();
        db.createUser(userId, firstName, lastName)
                .whenComplete(
                        (result, throwable) -> {
                            if (throwable != null) {
                                Toast.makeText(
                                                getBaseContext(),
                                                R.string.database_error,
                                                Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                startActivity(intent);
                            }
                        });
            // Put the current user id in cache
            Utils.putStringInPrefs(this, "user_id", userId);
    }

    private void logInInstead() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }
}
