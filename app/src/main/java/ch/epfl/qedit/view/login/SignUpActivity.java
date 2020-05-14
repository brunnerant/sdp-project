package ch.epfl.qedit.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class SignUpActivity extends AppCompatActivity {

    private EditText firstNameField,
            lastNameField,
            emailField,
            passwordField,
            passwordConfirmationField;
    private Button signUpButton;
    private ProgressBar progressBar;

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

    private void signUp() {
        progressBar.setVisibility(View.VISIBLE);

        firstName = firstNameField.getText().toString();
        lastName = lastNameField.getText().toString();
        String email, password;
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "email empty", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "password empty", Toast.LENGTH_LONG).show();
            return;
        }

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
