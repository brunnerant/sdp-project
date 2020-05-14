package ch.epfl.qedit;

import static ch.epfl.qedit.view.login.Util.USER;

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
 * This ghost activity is launch first in the QEDit app. It redirects the flow on the Home activity
 * if a user has previously logged in, or in the LogIn activity otherwise.
 */
public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
