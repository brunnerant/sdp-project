package ch.epfl.qedit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import ch.epfl.qedit.backend.database.FirebaseDBService;
import ch.epfl.qedit.view.home.HomeActivity;
import ch.epfl.qedit.view.login.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends Activity {

    public static final String USER = "ch.epfl.qedit.view.USER";

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
                                    Toast.makeText(
                                                    getBaseContext(),
                                                    R.string.database_error,
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    Intent intent = new Intent(this, HomeActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(USER, result);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    finish();
                                }
                            });
        } else {
            // No user is signed in
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
