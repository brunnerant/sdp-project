package ch.epfl.qedit.backend.permission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This class should be derived by all the activities that require permissions from the android
 * system. It will take care of forwarding all permissions to the centralized permission manager.
 * That way, the permission manager can be mocked, allowing more flexible tests.
 */
public class PermissionActivity extends AppCompatActivity {

    @Override
    public final void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // This cast is a bit unpleasant, but necessary. We know for sure that only in the
        // real permission manager this method will be called, so we can safely cast the
        // permission manager to the correct type, and forward it the response.
        AndroidPermManager manager = (AndroidPermManager) PermManagerFactory.getInstance();
        manager.onRequestPermissionResult(requestCode, grantResults);
    }
}
