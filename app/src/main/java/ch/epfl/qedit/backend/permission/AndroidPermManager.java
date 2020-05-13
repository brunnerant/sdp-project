package ch.epfl.qedit.backend.permission;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.Map;

public class AndroidPermManager implements PermissionManager, ActivityCompat.OnRequestPermissionsResultCallback {

    private final Context context;

    // This is the map of pending requests to the android permission system. nextRequestId
    // is used to generate unique ids for each request. I assumed that 2^31 values is well
    // enough for the requestId not to overflow.
    private int nextRequestId;
    private final Map<Integer, OnPermissionResult> callbacks;

    public AndroidPermManager(Context context) {
        this.context = context;
        this.nextRequestId = 0;
        this.callbacks = new HashMap<>();
    }

    @Override
    public boolean checkPermission(String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void requestPermissions(String[] permissions, OnPermissionResult callback) {
        callbacks.put(nextRequestId++, callback);
        ActivityCompat.requestPermissions(this, permissions, nextRequestId);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != REQUEST_CODE || permissions.length != REQUESTED_PERMISSIONS.length)
            return;

        for (int i = 0; i < REQUESTED_PERMISSIONS.length; i++) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                // If the permissions were not granted, we just show an error UI to the user
                setErrorUI();
                return;
            }
        }

        locService.subscribe(this, LOCATION_INTERVAL);
    }
}
