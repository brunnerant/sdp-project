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

import ch.epfl.qedit.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button loginButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginUserAccount();
                    }
                });
    }

    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Login successful",
                                            Toast.LENGTH_LONG)
                                            .show();
                                    progressBar.setVisibility(View.GONE);

                                    Intent intent =
                                            new Intent(LogInActivity.this, TokenLogInActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Login failed",
                                            Toast.LENGTH_LONG)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
    }

    private void initializeUI() {
        emailField = findViewById(R.id.field_email);
        passwordField = findViewById(R.id.field_password);

        loginButton = findViewById(R.id.button_log_in);
        progressBar = findViewById(R.id.progress_bar);
    }
}
