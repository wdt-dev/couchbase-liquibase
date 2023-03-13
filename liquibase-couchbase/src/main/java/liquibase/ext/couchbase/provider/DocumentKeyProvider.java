package liquibase.ext.couchbase.provider;

/**
 * Document key provider interface
 * K - source object type required by key provider
 * R - key type
 */
public interface DocumentKeyProvider<R, K> {

    R getKey(K object);
}
