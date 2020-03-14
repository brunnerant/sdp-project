package ch.epfl.qedit.backend.auth;

/**
 * This factory class allows the frontend classes to retrieve the singleton authentication service
 * without caring about its implementation.
 */
public final class AuthenticationFactory {
    /** The singleton instance of the auth service */
    private static AuthenticationService authService = null;

    public static AuthenticationService getInstance() {
        if (authService == null) authService = new MockAuthService();

        return authService;
    }

    public static void setInstance(AuthenticationService service) {
        authService = service;
    }
}
