package ch.epfl.qedit.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.AuthenticationService;
import ch.epfl.qedit.backend.MockAuthService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;

public class LoginActivity extends AppCompatActivity {

    public static final String USER = "ch.epfl.qedit.view.USER";

    private EditText usernameText;
    private EditText passwordText;
    private ProgressBar progressBar;

    private AuthenticationService authService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = findViewById(R.id.login_username_text);
        passwordText = findViewById(R.id.login_password_text);
        progressBar = findViewById(R.id.login_progress_bar);

        authService = new MockAuthService();
        handler =
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        progressBar.setVisibility(View.GONE);
                        AuthenticationService.LoginResponse response =
                                (AuthenticationService.LoginResponse) msg.obj;

                        if (response.successful()) onLoginSuccessful(response.getUser());
                        else onLoginFailed(response.getError());

                        super.handleMessage(msg);
                    }
                };
    }

    public void handleLogin(View view) {
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        progressBar.setVisibility(View.VISIBLE);

        authService.sendRequest(
                new AuthenticationService.LoginRequest(username, password),
                new Callback<AuthenticationService.LoginResponse>() {
                    @Override
                    public void onReceive(AuthenticationService.LoginResponse response) {
                        final AuthenticationService.LoginResponse _response = response;
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        if (_response.successful())
                                            onLoginSuccessful(_response.getUser());
                                        else onLoginFailed(_response.getError());
                                    }
                                });
                    }
                });
    }

    public void onLoginSuccessful(User user) {
        Intent intent = new Intent(LoginActivity.this, ViewRoleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onLoginFailed(AuthenticationService.LoginResponse.Error error) {
        int stringId = 0;
        switch (error) {
            case ConnectionError:
                stringId = R.string.connection_error_message;
                break;
            case UserDoesNotExist:
                stringId = R.string.wrong_username_message;
                break;
            case WrongPassword:
                stringId = R.string.wrong_password_message;
                break;
        }

        Toast toast =
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(stringId),
                        Toast.LENGTH_SHORT);
        toast.show();
    }
}
