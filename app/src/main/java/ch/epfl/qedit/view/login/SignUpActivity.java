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

public class SignUpActivity extends AppCompatActivity {

    private EditText emailField, passwordField;
    private Button signUpButton;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        initializeUI();

        signUpButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerNewUser();
                    }
                });
    }

    private void registerNewUser() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailField.getText().toString();
        password = passwordField.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Registration successful!",
                                            Toast.LENGTH_LONG)
                                            .show();
                                    progressBar.setVisibility(View.GONE);

                                    Intent intent =
                                            new Intent(
                                                    SignUpActivity.this, LogInActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Registration failed! Please try again later",
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
        signUpButton = findViewById(R.id.button_sign_up);
        progressBar = findViewById(R.id.progress_bar);
    }
}
