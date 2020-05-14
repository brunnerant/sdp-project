package ch.epfl.qedit.view.login;

import static ch.epfl.qedit.util.Utils.checkString;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.AuthenticationService;
import ch.epfl.qedit.backend.database.FirebaseDBService;
import ch.epfl.qedit.util.Utils;
import java.util.function.Predicate;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstNameField,
            lastNameField,
            emailField,
            passwordField,
            passwordConfirmationField;
    private Button signUpButton;
    private ProgressBar progressBar;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    private AuthenticationService auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = AuthenticationFactory.getInstance();

        initializeViews();

        signUpButton.setOnClickListener(v -> signUp());
    }

    private void initializeViews() {
        firstNameField = findViewById(R.id.field_first_name);
        lastNameField = findViewById(R.id.field_last_name);
        emailField = findViewById(R.id.field_email);
        passwordField = findViewById(R.id.field_password);
        passwordConfirmationField = findViewById(R.id.field_password_confirmation);
        signUpButton = findViewById(R.id.button_sign_up);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void confirmPassword() {
        String confirmation = passwordConfirmationField.getText().toString();
    }

    private boolean checkInputValidity() {
        // Check validity of all input
        Predicate<String> emailFormat = str -> str.matches(Utils.REGEX_EMAIL);
        Predicate<String> passwordFormat = str -> str.length() >= 6;
        Predicate<String> nameFormat = str -> str.matches(Utils.REGEX_NAME);
        email = checkString(emailField, emailFormat, getResources(), R.string.invalid_email);
        password =
                checkString(
                        passwordField, passwordFormat, getResources(), R.string.invalid_password);
        firstName = checkString(firstNameField, nameFormat, getResources(), R.string.invalid_name);
        lastName = checkString(lastNameField, nameFormat, getResources(), R.string.invalid_name);

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

        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);

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
    }
}
