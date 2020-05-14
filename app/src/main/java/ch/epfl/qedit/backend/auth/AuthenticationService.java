package ch.epfl.qedit.backend.auth;

import java.util.concurrent.CompletableFuture;

/**
 * Represents an authentication service, that is a service to which login requests can be sent. It
 * handles asynchronous responses by using a callback.
 */
public interface AuthenticationService {

    CompletableFuture<String> signUp(String email, String password);

    CompletableFuture<String> logIn(String email, String password);
}
