package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.exception.ProvideKeyFailedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FieldDocumentKeyProviderTest {

    @Test
    void Should_get_json_key_value() {
        String keyField = "key";
        FieldDocumentKeyProvider fieldDocumentKeyProvider = new FieldDocumentKeyProvider(keyField);
        String expected = "expected";

        JsonObject jsonObject = mock(JsonObject.class);
        when(jsonObject.containsKey(keyField)).thenReturn(true);
        when(jsonObject.getString(keyField)).thenReturn(expected);

        assertThat(fieldDocumentKeyProvider.getKey(jsonObject)).isEqualTo(expected);
    }

    @Test
    void Should_throw_if_key_missed() {
        String keyField = "key";
        FieldDocumentKeyProvider fieldDocumentKeyProvider = new FieldDocumentKeyProvider(keyField);

        JsonObject jsonObject = mock(JsonObject.class);
        when(jsonObject.containsKey(keyField)).thenReturn(false);

        assertThatExceptionOfType(ProvideKeyFailedException.class).isThrownBy(
                () -> fieldDocumentKeyProvider.getKey(jsonObject));
    }
}
