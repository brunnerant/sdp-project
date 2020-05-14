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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.database.FirebaseDBService;
import ch.epfl.qedit.util.Utils;
import ch.epfl.qedit.view.home.HomeActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button signUpButton;
    private ProgressBar progressBar;

    private String firstName = "jsdf";
    private String lastName = "dhdf";

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();

        firstName = "moi";
        lastName = "toi";

        signUpButton.setOnClickListener(v -> signUp());
    }

    private void initializeViews() {
        emailField = findViewById(R.id.field_email);
        passwordField = findViewById(R.id.field_password);
        signUpButton = findViewById(R.id.button_sign_up);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void signUp() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "email empty", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "password empty", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                onSignUpSuccessful();
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "sign up fail",
                                        Toast.LENGTH_LONG)
                                        .show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
    }

    private void onSignUpSuccessful() {
        Toast.makeText(
                getApplicationContext(),
                "sign up success",
                Toast.LENGTH_LONG)
                .show();
        progressBar.setVisibility(View.GONE);


        Intent intent =
                new Intent(
                        SignUpActivity.this, LogInActivity.class);


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // User is signed in
            String email = firebaseUser.getEmail();
            String uid = firebaseUser.getUid();

            FirebaseDBService firebaseDBService = new FirebaseDBService();
            firebaseDBService.createUser(uid, firstName, lastName).whenComplete(
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
                    }
            );

        } else {
            // No user is signed in
            //onLogInFailed();
        }

    }
}
