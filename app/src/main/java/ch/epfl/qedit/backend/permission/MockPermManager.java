package ch.epfl.qedit.backend.permission;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is used to mock the android permission manager. It can be used to test what happens
 * when the app does not have a permission and has to request it. Then, the mock can be used
 * to control whether the permission is granted or denied.
 */
public class MockPermManager implements PermissionManager {

    // The of permissions that were granted to the app.
    private final Set<String> granted;

    // The set of permissions that will be denied, if the app request them
    private final Set<String> toDeny;

    public MockPermManager() {
        this.granted = new HashSet<>();
        this.toDeny = new HashSet<>();
    }

    /**
     * Grants the given permissions so that the next time the app checks whether they are
     * allowed, it will return true.
     * @param permissions the permissions to grant.
     */
    public void grantPermissions(String... permissions) {
        granted.addAll(Arrays.asList(permissions));
    }

    /**
     * Denies the permissions such that they are not granted, and if the app request the
     * permissions, it will not be accepted. This is useful to test certain scenarios in
     * test cases involving permissions.
     * @param permissions
     */
    public void denyPermissions(String... permissions) {
        for (String permission : permissions) {
            granted.remove(permission);
            toDeny.add(permission);
        }
    }

    /**
     * Resets the given permissions, so that they are no longer granted, but will be accepted
     * if the app requests them.
     * @param permissions the permissions to reset.
     */
    public void resetPermissions(String... permissions) {
        granted.removeAll(Arrays.asList(permissions));
        toDeny.removeAll(Arrays.asList(permissions));
    }

    @Override
    public boolean checkPermission(PermissionActivity activity, String permission) {
        return granted.contains(permission);
    }

    @Override
    public void requestPermissions(PermissionActivity activity, String[] permissions, OnPermissionResult callback) {
        boolean[] result = new boolean[permissions.length];

        for (int i = 0; i < result.length; i++) {
            if (granted.contains(permissions[i])) {
                // If the permission was already granted, we don't change anything
                result[i] = true;
            } else if (toDeny.contains(permissions[i])) {
                // If we must deny it, we don't accept the request
                result[i] = false;
            } else {
                // If it was not already granted, accept it, and mark it as granted
                granted.add(permissions[i]);
                result[i] = true;
            }
        }

        // We don't pass the result immediately, because it could cause issues. It is not a good
        // idea to grant permissions before the request was terminated, so we wait a little bit.
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                callback.handle(result);
            }
        }, 1000);
    }
}
