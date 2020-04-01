package ch.epfl.qedit.util;

/**
 * Represents a response that a certain service can send after a request.
 *
 * @param <T> the type of the data when the response is successful
 */
public class Response<T> {
    public static final Error NO_ERROR = new Error(0);

    private final T data;
    private final Error error;

    private Response(T data, Error error) {
        this.data = data;
        this.error = error;
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(data, NO_ERROR);
    }

    public static <T> Response<T> error(Error error) {
        return new Response<>(null, error);
    }

    public T getData() {
        return data;
    }

    public Error getError() {
        return error;
    }
}
