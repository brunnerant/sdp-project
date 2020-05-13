package ch.epfl.qedit.backend.auth;

import ch.epfl.qedit.R;
import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;
import ch.epfl.qedit.util.Error;
import ch.epfl.qedit.util.Response;

/**
 * Represents an authentication service, that is a service to which login requests can be sent. It
 * handles asynchronous responses by using a callback.
 */
public interface AuthenticationService {

    Error CONNECTION_ERROR = new Error(R.string.connection_error);
    Error WRONG_TOKEN = new Error(R.string.invalid_token);

    /**
     * Sends a request to the authentication service, and receives the response asynchronously
     * through the callback.
     *
     * @param token the token to send to the authentication service
     * @param responseCallback the callback to handle the response once it arrives
     */
    void sendRequest(String token, Callback<Response<User>> responseCallback);
}
