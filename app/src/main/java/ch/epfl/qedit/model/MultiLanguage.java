package ch.epfl.qedit.model;

/**
 * This interface is used for all the components of the model that should support several languages.
 * It allows a generic component to be instantiated by replacing the generic string ids with the
 * instances for the wanted language using the given string pool. The string pool is a mapping from
 * string ids to the value of the strings.
 *
 * @param <T> the type of the instantiated component, usually the self type
 */
public interface MultiLanguage<T> {
    /**
     * Instantiates the component with the given string pool. This should replace all generic string
     * ids by the values given by the pool.
     *
     * @param pool the string pool to lookup string ids into
     * @return an instantiated component
     */
    T instantiateLanguage(StringPool pool);
}
