package ch.epfl.qedit.util;

/**
 * Represents object that can be transformed from/to a bundle. This is useful for local caching, and
 */
public interface Bundlable {
    /**
     * Converts the object to a bundle, that can be later sent to a database or to a local cache.
     *
     * @return the converted object
     */
    BundledData toBundle();
}
