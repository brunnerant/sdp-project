package ch.epfl.qedit.backend.auth;

import java.util.concurrent.CompletableFuture;

/**
 * Represents an authentication service, that is a service to which login requests can be sent. It
 * handles asynchronous responses by using a callback.
 */
public interface AuthenticationService {

    /**
     * Returns the user that is currently logged in the application, or null if nobody is logged in
     * yet.
     *
     * @return the currently logged-in user, or null if nobody is.
     */
    String getUser();

    /**
     * Sign Up a new user in the Authentication service. It associate a new ID to each unique pairs
     * of email-password.
     *
     * @param email of the new user
     * @param password of the new user
     * @return future that hold an error if sign up fail or the new ID of the user if sign up
     *     succeed
     */
    CompletableFuture<String> signUp(String email, String password);

    /**
     * Check if the pair email-password is in the Authentication service and return the ID of the
     * user if it does.
     *
     * @param email of the user
     * @param password of the user
     * @return future that hold an error if log in fail or the new ID of the user if log in succeed
     */
    CompletableFuture<String> logIn(String email, String password);
}
