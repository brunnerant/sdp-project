package ch.epfl.qedit.backend.permission;

/**
 * This interface's goal is to hide the details behind the android permission system, in order to
 * enable us to mock it. This way, the tests will no longer have to rely on the android permission
 * system, but rather on our mocked system, which will make them more flexible.
 */
public interface PermissionManager {
    /**
     * This functional interface represents a callback that is triggered when the user granted or
     * denied the permissions that were requested.
     */
    interface OnPermissionResult {
        void handle(boolean[] granted);
    }

    /**
     * This method returns whether the given permission is granted or not. The permission is
     * represented as a string, which can be accessed from Manifest.permission.
     *
     * @param activity the activity checking the permission.
     * @param permission the permission, as a string.
     * @return true iff the permission is granted to the app.
     */
    boolean checkPermission(PermissionActivity activity, String permission);

    /**
     * Returns whether the given permission was denied forever by the user (by clicking the "don't
     * show me again" button). If this is the case, any subsequent request to this manager will
     * fail, so the activity has to give up obtaining that permission.
     *
     * @param activity the activity checking the permission.
     * @param permission the permission to check.
     * @return true iff the permission was denied forever, and if it should no longer be asked.
     */
    boolean shouldAskAgain(PermissionActivity activity, String permission);

    /**
     * Requests the given permissions asynchronously, and triggers the given callback once the user
     * responded to the request.
     *  @param activity the activity requesting the permissions
     * @param callback the callback that will be triggered once the user answered.
     * @param permissions the permissions that the app wants to request.
     */
    void requestPermissions(
            PermissionActivity activity, OnPermissionResult callback, String... permissions);
}
