package ch.epfl.qedit.util;

/**
 * Represents a response that a certain service can send after a request.
 * @param <T> the type of the data when the response is successful
 */
public class Response<T> {
    public static final int NO_ERROR = 0;

    private final T data;
    private final int error;

    private Response(T data, int error) {
        this.data = data;
        this.error = error;
    }

    public static <T> Response<T> ok(T data) {
        return new Response<>(data, NO_ERROR);
    }

    public static <T> Response<T> error(int error) {
        return new Response<>(null, error);
    }

    public boolean successful() {
        return error == NO_ERROR;
    }

    public T getData() {
        return data;
    }

    public int getError() {
        return error;
    }
}
