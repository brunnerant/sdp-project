package ch.epfl.qedit.backend;

/**
 * This factory class is used to create dependency injection for testing, or for switching the
 * authentication service if several are available.
 */
public final class AuthenticationFactory {
    /** The singleton instance of the auth service */
    private static AuthenticationService authService = null;

    public static AuthenticationService getInstance() {
        if (authService == null)
            authService = new FirebaseAuthService();

        return authService;
    }

    public static void setInstance(AuthenticationService service) {
        authService = service;
    }
}
