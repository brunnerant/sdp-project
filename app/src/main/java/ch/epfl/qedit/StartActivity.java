package ch.epfl.qedit;

import static ch.epfl.qedit.view.login.Util.USER;
import static ch.epfl.qedit.view.login.Util.getStringInPrefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import ch.epfl.qedit.backend.database.FirebaseDBService;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.login.LogInActivity;
import ch.epfl.qedit.view.login.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This ghost activity is launched first in the QEDit app. It redirects the flow on the HomeActivity
 * if a user has previously logged in, or in the LogInActivity otherwise.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Offline automatic log in
        /*
        String uid = getUserIdInCache();
        if(userExistsInCache(uid)) {
            // User is signed in
            User user = ... // get user from cache
            launchHomeActivity(user);
        } else {
            // No user is signed in
            launchLoginActivity();
        }
        */

        // Online automatic log in
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            // User is signed in
            String uid = firebaseUser.getUid();

            FirebaseDBService firebaseDBService = new FirebaseDBService();
            firebaseDBService
                    .getUser(uid)
                    .whenComplete(
                            (result, throwable) -> {
                                if (throwable != null) {
                                    Util.showToast(
                                            R.string.database_error,
                                            getBaseContext(),
                                            getResources());
                                } else {
                                    launchHomeActivity(result);
                                }
                            });
        } else {
            // No user is signed in
            launchLoginActivity();
        }
    }

    private Boolean userExistsInCache(String uid) {
        return !uid.equals("");
    }

    private String getUserIdInCache() {
        return getStringInPrefs(this, "user_id");
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchHomeActivity(User user) {
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(USER, user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
