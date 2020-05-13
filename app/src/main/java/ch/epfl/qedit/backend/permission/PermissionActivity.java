package ch.epfl.qedit.backend.permission;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionActivity extends AppCompatActivity {

    @Override
    public final void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndroidPermManager manager = (AndroidPermManager) PermManagerFactory.getInstance();
        manager.onRequestPermissionResult(requestCode, grantResults);
    }
}
