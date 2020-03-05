package ch.epfl.qedit.view.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import ch.epfl.qedit.R;
import ch.epfl.qedit.view.ViewRoleActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "ch.epfl.qedit.view.MESSAGE";
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel =
                ViewModelProviders.of(this, new LoginViewModelFactory()).get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        getLoginFormState(usernameEditText, passwordEditText, loginButton);
        getLoginResult(loadingProgressBar);
        TextWatcher afterTextChangedListener = textWatcher(usernameEditText, passwordEditText);

        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        manageListeners(usernameEditText, passwordEditText, loginButton, loadingProgressBar);
    }

    private void getLoginFormState(
            final EditText usernameEditText,
            final EditText passwordEditText,
            final Button loginButton) {
        loginViewModel
                .getLoginFormState()
                .observe(
                        this,
                        new Observer<LoginFormState>() {
                            @Override
                            public void onChanged(@Nullable LoginFormState loginFormState) {
                                if (loginFormState == null) {
                                    return;
                                }
                                loginButton.setEnabled(loginFormState.isDataValid());
                                if (loginFormState.getUsernameError() != null) {
                                    usernameEditText.setError(
                                            getString(loginFormState.getUsernameError()));
                                }
                                if (loginFormState.getPasswordError() != null) {
                                    passwordEditText.setError(
                                            getString(loginFormState.getPasswordError()));
                                }
                            }
                        });
    }

    private void getLoginResult(final ProgressBar loadingProgressBar) {
        loginViewModel
                .getLoginResult()
                .observe(
                        this,
                        new Observer<LoginResult>() {
                            @Override
                            public void onChanged(@Nullable LoginResult loginResult) {
                                if (loginResult == null) {
                                    return;
                                }
                                loadingProgressBar.setVisibility(View.GONE);
                                if (loginResult.getError() != null) {
                                    showLoginFailed(loginResult.getError());
                                }
                                if (loginResult.getSuccess() != null) {
                                    updateUiWithUser(loginResult.getSuccess());

                                    EditText editTextUsername = findViewById(R.id.username);
                                    String username = editTextUsername.getText().toString();

                                    Intent intent =
                                            new Intent(LoginActivity.this, ViewRoleActivity.class);
                                    intent.putExtra(EXTRA_MESSAGE, username);

                                    startActivity(intent);
                                }
                                setResult(Activity.RESULT_OK);

                                // Complete and destroy login activity once successful
                                // finish();
                            }
                        });
    }

    private TextWatcher textWatcher(
            final EditText usernameEditText, final EditText passwordEditText) {
        TextWatcher afterTextChangedListener =
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // ignore
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // ignore
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        loginViewModel.loginDataChanged(
                                usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
                    }
                };

        return afterTextChangedListener;
    }

    private void manageListeners(
            final EditText usernameEditText,
            final EditText passwordEditText,
            final Button loginButton,
            final ProgressBar loadingProgressBar) {
        passwordEditText.setOnEditorActionListener(
                new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            loginViewModel.login(
                                    usernameEditText.getText().toString(),
                                    passwordEditText.getText().toString());
                        }
                        return false;
                    }
                });

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingProgressBar.setVisibility(View.VISIBLE);
                        loginViewModel.login(
                                usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
                    }
                });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), "Connexion réussie", Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), "Connexion échouée", Toast.LENGTH_SHORT).show();
    }
}
