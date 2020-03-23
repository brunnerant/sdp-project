package ch.epfl.qedit.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.AuthenticationService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.LocaleHelper;
import ch.epfl.qedit.util.Response;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String USER = "ch.epfl.qedit.view.USER";

    private EditText tokenText;
    private ProgressBar progressBar;

    private AuthenticationService authService;
    private Handler handler;

    private boolean userHasInteracted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        tokenText = findViewById(R.id.login_token);
        progressBar = findViewById(R.id.login_progress_bar);

        authService = AuthenticationFactory.getInstance();
        handler = new Handler();

        // Language selection
        Spinner languageSelectionSpinner = (Spinner) findViewById(R.id.language_selection);
        String currentLanguage = Locale.getDefault().getLanguage();
        int positionInLanguageList = 0;
        String[] languageList = getResources().getStringArray(R.array.languages_codes);
        for(int i = 0; i < languageList.length; ++i) {
            if(currentLanguage.equals(languageList[i])) positionInLanguageList = i;
        }
        languageSelectionSpinner.setSelection(positionInLanguageList, false);
        languageSelectionSpinner.setOnItemSelectedListener(this);
        setTitle(R.string.login);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userHasInteracted = true;
    }

    @Override
    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
        if (!userHasInteracted) {
            return;
        }
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.language_changed) + " "
                        + getResources().getStringArray(R.array.languages_list_translated)[pos],
                Toast.LENGTH_SHORT).show();

        String languageCode = getResources().getStringArray(R.array.languages_codes)[pos];
        LocaleHelper.setLocale(this, languageCode);
        this.recreate();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    public void handleLogin(View view) {
        String token = tokenText.getText().toString();
        // Sanitize token
        if (token.isEmpty()) {
            printShortToast(R.string.empty_token_message);
            return;
        }
        token = token.trim(); // Remove leading and trailing spaces in the token
        // This regular expression will accept only strings of length 20
        // and composed of letters and digits
        if (!token.matches("[a-zA-Z0-9]{20}")) {
            printShortToast(R.string.wrong_token_message);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        authService.sendRequest(
                token,
                new Callback<Response<User>>() {
                    @Override
                    public void onReceive(final Response<User> response) {
                        handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.GONE);
                                        if (response.successful())
                                            onLoginSuccessful(response.getData());
                                        else onLoginFailed(response.getError());
                                    }
                                });
                    }
                });
    }

    public void onLoginSuccessful(User user) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void onLoginFailed(int error) {
        int stringId = 0;
        switch (error) {
            case AuthenticationService.CONNECTION_ERROR:
                stringId = R.string.connection_error_message;
                break;
            case AuthenticationService.WRONG_TOKEN:
                stringId = R.string.wrong_token_message;
                break;
            default:
                break;
        }

        printShortToast(stringId);
    }

    private void printShortToast(int stringId) {
        Toast toast =
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(stringId),
                        Toast.LENGTH_SHORT);
        toast.show();
    }
}
