package ch.epfl.qedit;

import static ch.epfl.qedit.view.home.HomeActivity.USER;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import ch.epfl.qedit.backend.auth.AuthenticationFactory;
import ch.epfl.qedit.backend.auth.AuthenticationService;
import ch.epfl.qedit.backend.database.DatabaseFactory;
import ch.epfl.qedit.backend.database.DatabaseService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.login.LogInActivity;
import ch.epfl.qedit.view.login.Util;

/**
 * This ghost activity is launch first in the QEDit app. It redirects the flow on the Home activity
 * if a user has previously logged in, or in the LogIn activity otherwise.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userId = retrieveUserId(this);

        if (userId != null) {
            // If a user is already logged-in, we retrieve its information from the
            // database (or from the cache depending on the implementation of the service).
            retrieveUserAndLaunchHomeActivity(this, userId);
        } else {
            // No user is signed in, so we go to the login activity
            launchLoginActivity();
        }
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    public String retrieveUserId(Context context) {
        AuthenticationService authService = AuthenticationFactory.getInstance();
        return authService.getUser();
    }

    public void retrieveUserAndLaunchHomeActivity(Context context, String userId) {
        DatabaseService dbService = DatabaseFactory.getInstance(this);
        dbService
                .getUser(userId)
                .whenComplete(
                        (result, throwable) -> {
                            if (throwable != null) {
                                Util.showToast(
                                        R.string.database_error, getBaseContext(), getResources());
                            } else {
                                // Then, we launch the home activity
                                launchHomeActivity(this, result);
                            }
                        });
    }

    private void launchHomeActivity(Context context, User user) {
        Intent intent = new Intent(context, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
