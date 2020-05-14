package ch.epfl.qedit.view.login;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
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
import ch.epfl.qedit.backend.database.FirebaseDBService;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.util.Utils;
import ch.epfl.qedit.view.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String USER = "ch.epfl.qedit.view.USER";

    private EditText emailField, passwordField;
    private Button logInButton;
    private ProgressBar progressBar;
    private TextView textViewSignUp;

    private FirebaseAuth firebaseAuth;

    private boolean userHasInteracted = false;

    private Context context;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = FirebaseAuth.getInstance();

        context = getBaseContext();
        resources = getResources();

        initializeViews();

        logInButton.setOnClickListener(v -> logIn());
        textViewSignUp.setOnClickListener(v -> signUpInstead());

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
        Utils.showToastChangedLanguage(pos, Toast.LENGTH_SHORT, getApplicationContext(), resources);
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
        textViewSignUp = findViewById(R.id.not_signed_up);

        int colorNotSignedUpAlreadySignedUp =
                resources.getColor(R.color.colorNotSignedUpAlreadySignedUp);
        Spanned coloredText =
                Html.fromHtml(
                        "<font color='"
                                + colorNotSignedUpAlreadySignedUp
                                + "'>"
                                + resources.getString(R.string.not_signed_up)
                                + "</font>");
        textViewSignUp.setText(coloredText);
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

        int colorNotSignedUpAlreadySignedUp =
                resources.getColor(R.color.colorNotSignedUpAlreadySignedUp);
        Spanned coloredText =
                Html.fromHtml(
                        "<font color='"
                                + colorNotSignedUpAlreadySignedUp
                                + "'>"
                                + resources.getString(R.string.not_signed_up)
                                + "</font>");
        textViewSignUp.setText(coloredText);

        setTitle(resources.getString(R.string.title_activity_log_in));
    }

    private void logIn() {
        String email, password;
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        Boolean fail = false;

        Boolean emailIsEmpty = TextUtils.isEmpty(email);
        if (emailIsEmpty) {
            emailField.setError(resources.getString(R.string.input_cannot_be_empty));
            fail = true;
        }
        Boolean passwordIsEmpty = TextUtils.isEmpty(password);
        if (passwordIsEmpty) {
            passwordField.setError(resources.getString(R.string.input_cannot_be_empty));
            fail = true;
        }

        // Sanitize email
        email = email.trim(); // Remove leading and trailing spaces

        Boolean emailMatches = email.matches(Utils.regexEmail());
        if (!emailIsEmpty && !emailMatches) {
            emailField.setError(resources.getString(R.string.invalid_email));
            fail = true;
        }

        Boolean passwordIsLongEnough = password.length() >= 6;
        if (!passwordIsEmpty && !passwordIsLongEnough) {
            passwordField.setError(resources.getString(R.string.invalid_password));
            fail = true;
        }

        if (fail) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth
                .signInWithEmailAndPassword(email, password)
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
        Utils.showToast(
                R.string.log_in_success,
                Toast.LENGTH_SHORT,
                getApplicationContext(),
                resources); // TODO après
        progressBar.setVisibility(View.GONE);

        Intent intent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // User is signed in
            String uid = firebaseUser.getUid();

            FirebaseDBService firebaseDBService = new FirebaseDBService();
            firebaseDBService
                    .getUser(uid)
                    .whenComplete(
                            (result, throwable) -> {
                                if (throwable != null || result.getFirstName() == null) {
                                    Toast.makeText(
                                                    context,
                                                    R.string.database_error,
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    bundle.putSerializable(USER, result);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });

            // Put the current user id in cache
            Utils.putStringInPrefs(this, "user_id", uid);

        } else {
            // No user is signed in
            onLogInFailed();
        }
    }

    private void onLogInFailed() {
        Utils.showToast(
                R.string.log_in_fail, Toast.LENGTH_SHORT, getApplicationContext(), resources);
        progressBar.setVisibility(View.GONE);
    }

    private void signUpInstead() {
        Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
