package ch.epfl.qedit.backend.permission;

/**
 * This interface's goal is to hide the details behind the android permission system, in order
 * to enable us to mock it. This way, the tests will no longer have to rely on the android
 * permission system, but rather on our mocked system, which will make them more flexible.
 */
public interface PermissionManager {
    /**
     * This functional interface represents a callback that is triggered when the user
     * granted or denied the permissions that were requested.
     */
    interface OnPermissionResult {
        void handle(boolean[] granted);
    }

    /**
     * This method returns whether the given permission is granted or not. The permission is
     * represented as a string, which can be accessed from Manifest.permission.
     * @param permission the permission, as a string.
     * @return true iff the permission is granted to the app.
     */
    boolean checkPermission(String permission);

    /**
     * Requests the given permissions asynchronously, and triggers the given callback once the
     * user responded to the request.
     * @param permissions the permissions that the app wants to request.
     * @param callback the callback that will be triggered once the user answered.
     */
    void requestPermissions(String[] permissions, OnPermissionResult callback);
}
