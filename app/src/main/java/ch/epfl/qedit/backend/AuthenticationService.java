package ch.epfl.qedit.backend;

import ch.epfl.qedit.model.User;
import ch.epfl.qedit.util.Callback;

/**
 * Represents an authentication service, that is a service to which login requests can be sent. It
 * handles asynchronous responses by using a callback.
 */
public interface AuthenticationService {
    /** Represents a response made by the authentication service */
    class LoginResponse {
        public enum Error {
            WrongToken,
            ConnectionError
        }

        private final User user;
        private final Error error;

        private LoginResponse(User user, Error error) {
            this.user = user;
            this.error = error;
        }

        public static LoginResponse ok(User user) {
            return new LoginResponse(user, null);
        }

        public static LoginResponse error(Error error) {
            return new LoginResponse(null, error);
        }

        public boolean successful() {
            return user != null;
        }

        public User getUser() {
            return user;
        }

        public Error getError() {
            return error;
        }
    }

    /**
     * Sends a request to the authentication service, and receives the response asynchronously
     * through the callback.
     *
     * @param token the token to send to the authentication service
     * @param responseCallback the callback to handle the response once it arrives
     */
    void sendRequest(String token, Callback<LoginResponse> responseCallback);
}
