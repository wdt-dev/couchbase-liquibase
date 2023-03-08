package liquibase.ext.couchbase.provider;

public interface DocumentKeyProvider<R, K> {

    R getKey(K object);
}
