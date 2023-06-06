package liquibase.ext.couchbase.provider;

import liquibase.ext.couchbase.provider.generator.IncrementalKeyGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IncrementalDocumentKeyProviderTest {

    private final IncrementalKeyGenerator incrementalKeyGenerator = mock(IncrementalKeyGenerator.class);

    @Test
    void Should_call_generate() {
        IncrementalDocumentKeyProvider incrementalDocumentKeyProvider =
                new IncrementalDocumentKeyProvider(incrementalKeyGenerator);
        String expected = "expected";

        when(incrementalKeyGenerator.generate()).thenReturn(expected);

        assertThat(incrementalDocumentKeyProvider.getKey(null)).isEqualTo(expected);

        verify(incrementalKeyGenerator).generate();
    }
}
