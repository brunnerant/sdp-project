package ch.epfl.qedit.util;

public interface Visitable<V> {
    /** This method is used to implement the visitor pattern */
    void accept(V visitor);
}
