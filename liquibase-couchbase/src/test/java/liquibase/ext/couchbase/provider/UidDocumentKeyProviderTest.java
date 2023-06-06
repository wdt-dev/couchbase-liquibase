package liquibase.ext.couchbase.provider;

import liquibase.ext.couchbase.provider.generator.UidKeyGenerator;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UidDocumentKeyProviderTest {

    private final UidKeyGenerator uidKeyGenerator = mock(UidKeyGenerator.class);

    @Test
    void Should_call_generate() {
        UidDocumentKeyProvider uidDocumentKeyProvider = new UidDocumentKeyProvider(uidKeyGenerator);

        when(uidKeyGenerator.generate()).thenReturn("");

        uidDocumentKeyProvider.getKey(null);

        verify(uidKeyGenerator).generate();
    }
}
