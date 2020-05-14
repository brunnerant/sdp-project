package ch.epfl.qedit.backend.permission;

import android.app.Activity;
import android.content.Context;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to mock the android permission manager. It can be used to test what happens
 * when the app does not have a permission and has to request it. Then, the mock can be used to
 * control whether the permission is granted or denied.
 */
public class MockPermManager implements PermissionManager {

    /** This enum tries to approximates as closely as possible the android permission states. */
    public enum State {
        UnknownPositive, // The permission was not granted yet, but will be granted if asked
        UnknownNegative, // The permission was not granted yet, and will be refused if asked
        Granted, // The permission was granted
        Denied, // The permission was denied forever
    }

    // The state of the permissions. If a permission is not in the map, it is assumed to have
    // state UnkownPositive.
    private final Map<String, State> permStates;

    // This is for espresso tests to wait
    CountingIdlingResource idlingResource;

    public MockPermManager() {
        this.permStates = new HashMap<>();
        this.idlingResource = new CountingIdlingResource("MockPermService");
    }

    /**
     * Resets the given permissions, so that they are no longer granted, but will be accepted if the
     * app requests them.
     *
     * @param permissions the permissions to reset.
     */
    public void resetPermissions(String... permissions) {
        for (String permission : permissions) permStates.put(permission, State.UnknownPositive);
    }

    /**
     * Grants the given permissions so that the next time the app checks whether they are allowed,
     * it will return true.
     *
     * @param permissions the permissions to grant.
     */
    public void grantPermissions(String... permissions) {
        for (String permission : permissions) permStates.put(permission, State.Granted);
    }

    /**
     * Refuse the permission next time the app request them. To refuse them forever, use
     * denyPermissions.
     *
     * @param permissions the permissions to refuse.
     */
    public void refusePermissions(String... permissions) {
        for (String permission : permissions) permStates.put(permission, State.UnknownNegative);
    }

    /**
     * Denies the given permissions forever, meaning they will always be refused.
     *
     * @param permissions the permissions to deny.
     */
    public void denyPermissions(String... permissions) {
        for (String permission : permissions) permStates.put(permission, State.Denied);
    }

    // Returns the state of the given permission. If it is not in the map of permissions states,
    // we assume that it has state UnknownPositive.
    private State getState(String permission) {
        return permStates.getOrDefault(permission, State.UnknownPositive);
    }

    @Override
    public boolean checkPermission(Context context, String permission) {
        return getState(permission) == State.Granted;
    }

    @Override
    public boolean shouldAskAgain(Activity activity, String permission) {
        State state = getState(permission);

        // We should only keep asking if the permission was not granted, and if the user
        // didn't deny it forever (don't ask again button).
        return state != State.Denied && state != State.Granted;
    }

    @Override
    public void requestPermissions(
            PermissionActivity activity, OnPermissionResult callback, String... permissions) {
        boolean[] result = new boolean[permissions.length];

        for (int i = 0; i < result.length; i++) {
            switch (getState(permissions[i])) {
                case UnknownPositive:
                    // We grant the permission if it is unknown positive
                    permStates.put(permissions[i], State.Granted);
                case Granted:
                    // In this case, it was already granted
                    result[i] = true;
                    break;
                case UnknownNegative:
                case Denied:
                    // In those cases, we refuse the permission
                    result[i] = false;
                    break;
            }
        }

        // We don't pass the result immediately, because it could cause issues. It is not a good
        // idea to grant permissions before the request was terminated, so we wait a little bit.
        idlingResource.increment();
        new Thread(
                        () -> {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            callback.onPermissionResult(permissions, result);
                            idlingResource.decrement();
                        })
                .run();
    }

    public IdlingResource getIdlingResource() {
        return idlingResource;
    }
}
