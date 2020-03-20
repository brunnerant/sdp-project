package ch.epfl.qedit.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import ch.epfl.qedit.R;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.AuthenticationService;
import ch.epfl.qedit.backend.locale.ChangeLocale;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Response;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String USER = "ch.epfl.qedit.view.USER";

    private EditText tokenText;
    private ProgressBar progressBar;

    private AuthenticationService authService;
    private Handler handler;

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
        languageSelectionSpinner.setOnItemSelectedListener(this);
    }

    // TODO debug
    @Override
    public void onItemSelected(AdapterView parent, View view, int pos, long id) {
        String language = parent.getItemAtPosition(pos).toString();
        Toast.makeText(getApplicationContext(),
                language, Toast.LENGTH_SHORT)
                .show();
        String languageCode = getResources().getStringArray(R.array.language_codes)[pos];
/*

        Locale locale = new Locale("language");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        this.getApplicationContext().getResources().updateConfiguration(config, null);
*/
/*
        String currentLanguage = ChangeLocale.getLanguage(this);
        //System.out.println("curr = " + currentLanguage + " chosen = " + languageCode);
        if (!currentLanguage.equals(languageCode)) {
            ChangeLocale.onAttach(this, languageCode);
            //this.recreate();
        }
*/



/*
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        this.setContentView(R.layout.activity_login);

*/
/*
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(language_code.toLowerCase())); // API 17+ only.
        // Use conf.locale = new Locale(...) if targeting lower versions
        res.updateConfiguration(conf, dm);*/
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
/*
    // TODO debug
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(ChangeLocale.onAttach(base, "fr"));
    }
*/
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
