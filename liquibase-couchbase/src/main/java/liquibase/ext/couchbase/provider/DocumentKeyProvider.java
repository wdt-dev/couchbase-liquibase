package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;

/**
 * Document key provider interface
 * K - source object type required by key provider
 * R - key type
 */
public interface DocumentKeyProvider {

    String getKey(JsonObject object);
}
