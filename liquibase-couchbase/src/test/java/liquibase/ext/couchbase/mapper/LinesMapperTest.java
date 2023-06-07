package liquibase.ext.couchbase.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import liquibase.ext.couchbase.provider.DocumentKeyProvider;
import liquibase.ext.couchbase.provider.factory.DocumentKeyProviderFactory;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class LinesMapperTest {

    @Mock
    private DocumentKeyProviderFactory documentKeyProviderFactory;
    @Mock
    private DocumentKeyProvider documentKeyProvider;
    @Mock
    private File file;

    private final AtomicLong keyHolder = new AtomicLong(1L);
    private LinesMapper linesMapper;

    @BeforeEach
    public void setUp() {
        this.linesMapper = new LinesMapper(documentKeyProviderFactory);
    }

    @Test
    void Should_map_file_successfully() {
        when(file.lines()).thenReturn(Stream.of("{}", "{}", "{}"));
        when(documentKeyProviderFactory.getKeyProvider(any())).thenReturn(documentKeyProvider);
        when(documentKeyProvider.getKey(any())).thenAnswer((args) -> String.valueOf(keyHolder.getAndIncrement()));

        List<Document> expected = new ArrayList<>();
        Document doc1 = new Document(String.valueOf(keyHolder.get()), new Value("{}", DataType.JSON));
        Document doc2 = new Document(String.valueOf(keyHolder.get() + 1), new Value("{}", DataType.JSON));
        Document doc3 = new Document(String.valueOf(keyHolder.get() + 2), new Value("{}", DataType.JSON));
        expected.add(doc1);
        expected.add(doc2);
        expected.add(doc3);

        List<Document> result = linesMapper.map(file);
        assertThat(result).isEqualTo(expected);

        verify(documentKeyProviderFactory).getKeyProvider(any());
        verify(documentKeyProvider, times(3)).getKey(any());
    }
}
