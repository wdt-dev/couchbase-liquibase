package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.exception.ProvideKeyFailedException;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class FromDocumentKeyProvider implements DocumentKeyProvider<String, JsonObject> {
    private static final String DEFAULT_KEY_FIELD = "id";
    private final String keyField;

    public FromDocumentKeyProvider(String keyField) {
        this.keyField = isEmpty(keyField) ? DEFAULT_KEY_FIELD : keyField;
    }

    @Override
    public String getKey(JsonObject jsonObject) {
        if (jsonObject.containsKey(keyField)) {
            return (String) jsonObject.get(keyField);
        }
        throw new ProvideKeyFailedException("Document contains no key field");
    }
}
