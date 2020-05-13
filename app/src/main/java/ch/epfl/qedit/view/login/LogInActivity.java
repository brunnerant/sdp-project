package ch.epfl.qedit.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ch.epfl.qedit.R;

public class LogInActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        firebaseAuth = FirebaseAuth.getInstance();

        initializeViews();

        loginButton.setOnClickListener(v -> logIn());
    }

    private void initializeViews() {
        emailField = findViewById(R.id.field_email);
        passwordField = findViewById(R.id.field_password);
        loginButton = findViewById(R.id.button_log_in);
        progressBar = findViewById(R.id.progress_bar);
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
                                Toast.makeText(
                                        getApplicationContext(),
                                        "log in success",
                                        Toast.LENGTH_LONG)
                                        .show();
                                progressBar.setVisibility(View.GONE);

                                Intent intent =
                                        new Intent(LogInActivity.this, TokenLogInActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "log in fail",
                                        Toast.LENGTH_LONG)
                                        .show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
    }
}
