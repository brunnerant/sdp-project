package ch.epfl.qedit.util;

/** This interface represents a callback, which can be used to receive data asynchronously. */
public interface Callback<T> {
    void onReceive(T data);
}
