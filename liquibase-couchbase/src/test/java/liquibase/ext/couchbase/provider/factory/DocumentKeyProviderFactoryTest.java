package liquibase.ext.couchbase.provider.factory;

import liquibase.ext.couchbase.provider.ExpressionDocumentKeyProvider;
import liquibase.ext.couchbase.provider.FieldDocumentKeyProvider;
import liquibase.ext.couchbase.provider.IncrementalDocumentKeyProvider;
import liquibase.ext.couchbase.provider.UidDocumentKeyProvider;
import liquibase.ext.couchbase.types.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static liquibase.ext.couchbase.types.KeyProviderType.DEFAULT;
import static liquibase.ext.couchbase.types.KeyProviderType.EXPRESSION;
import static liquibase.ext.couchbase.types.KeyProviderType.INCREMENT;
import static liquibase.ext.couchbase.types.KeyProviderType.UID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class DocumentKeyProviderFactoryTest {

    private final File file = mock(File.class);
    private final DocumentKeyProviderFactory documentKeyProviderFactory = new DocumentKeyProviderFactory();

    @BeforeEach
    void setUp() {
        reset(file);
    }

    @Test
    void Should_return_default() {
        when(file.getKeyProviderType()).thenReturn(DEFAULT);

        assertThat(documentKeyProviderFactory.getKeyProvider(file)).isInstanceOf(FieldDocumentKeyProvider.class);
    }

    @Test
    void Should_return_uid() {
        when(file.getKeyProviderType()).thenReturn(UID);

        assertThat(documentKeyProviderFactory.getKeyProvider(file)).isInstanceOf(UidDocumentKeyProvider.class);
    }

    @Test
    void Should_return_incremental() {
        when(file.getKeyProviderType()).thenReturn(INCREMENT);

        assertThat(documentKeyProviderFactory.getKeyProvider(file)).isInstanceOf(IncrementalDocumentKeyProvider.class);
    }

    @Test
    void Should_return_expression() {
        when(file.getKeyProviderType()).thenReturn(EXPRESSION);
        when(file.getKeyProviderExpression()).thenReturn("#a, #b");

        assertThat(documentKeyProviderFactory.getKeyProvider(file)).isInstanceOf(ExpressionDocumentKeyProvider.class);
    }

}
