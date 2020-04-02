package ch.epfl.qedit.util;

/**
 * This interface describes the behavior of visitors
 *
 * @param <V> The type of visitor
 */
public interface Visitable<V> {
    /** This method is used to implement the visitor pattern */
    void accept(V visitor);
}
