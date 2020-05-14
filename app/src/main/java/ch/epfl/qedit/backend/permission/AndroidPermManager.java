package ch.epfl.qedit.backend.permission;

import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the real implementation of the permission manager. Due to how android works, we are
 * obliged to rely on the permission activity class to send the permission results to this manager.
 */
public class AndroidPermManager implements PermissionManager {

    // This is the map of pending requests to the android permission system. nextRequestId
    // is used to generate unique ids for each request. I assumed that 2^31 values is well
    // enough for the requestId not to overflow.
    private final Map<Integer, OnPermissionResult> callbacks;
    private int nextRequestCode;

    public AndroidPermManager() {
        this.nextRequestCode = 0;
        this.callbacks = new HashMap<>();
    }

    @Override
    public boolean checkPermission(PermissionActivity activity, String permission) {
        return ActivityCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean shouldAskAgain(PermissionActivity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    @Override
    public void requestPermissions(
            PermissionActivity activity, OnPermissionResult callback, String... permissions) {
        callbacks.put(nextRequestCode++, callback);
        ActivityCompat.requestPermissions(activity, permissions, nextRequestCode);
    }

    // This method will be called by the permission activity when a permission result arrives.
    // This design makes it possible to mock the permission manager, because it is the central
    // class through which all requests and responses go.
    void onRequestPermissionResult(int requestCode, int[] grantResults) {
        boolean[] granted = new boolean[grantResults.length];

        for (int i = 0; i < granted.length; i++)
            granted[i] = grantResults[i] == PackageManager.PERMISSION_GRANTED;

        OnPermissionResult callback = callbacks.get(requestCode);
        callbacks.remove(requestCode);
        callback.handle(granted);
    }
}
