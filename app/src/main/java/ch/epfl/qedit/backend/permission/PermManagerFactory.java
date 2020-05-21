package ch.epfl.qedit.backend.permission;

/** This class is used to retrieve the singleton */
public class PermManagerFactory {
    /** The singleton instance of the permission manager */
    private static PermissionManager permManager = null;

    private PermManagerFactory() {}

    public static PermissionManager getInstance() {
        if (permManager == null) permManager = new AndroidPermManager();

        return permManager;
    }

    public static void setInstance(PermissionManager manager) {
        permManager = manager;
    }
}
