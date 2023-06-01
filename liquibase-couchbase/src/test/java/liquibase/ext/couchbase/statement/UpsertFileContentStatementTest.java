package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.ImportType;
import liquibase.ext.couchbase.types.KeyProviderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UpsertFileContentStatementTest {

    private final TransactionAttemptContext transaction = mock(TransactionAttemptContext.class);
    private final ReactiveTransactionAttemptContext reactiveTransactionAttemptContext =
            mock(ReactiveTransactionAttemptContext.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final CollectionOperator collectionOperator = mock(CollectionOperator.class);

    private final File file = mock(File.class);

    @BeforeEach
    public void configure() {
        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collectionOperator);

        when(file.getImportType()).thenReturn(ImportType.LIST);
        when(file.getKeyProviderType()).thenReturn(KeyProviderType.DEFAULT);
    }

    @Test
    void Should_execute_in_transaction() {
        UpsertFileContentStatement statement = new UpsertFileContentStatement(TEST_KEYSPACE, file);

        statement.doInTransaction(transaction, clusterOperator);

        verify(collectionOperator).upsertDocsTransactionally(eq(transaction), anyList());
    }

    @Test
    void Should_execute_in_transaction_reactive() {
        UpsertFileContentStatement statement = new UpsertFileContentStatement(TEST_KEYSPACE, file);
        Flux<TransactionGetResult> mockedPublisher = mock(Flux.class);
        when(collectionOperator.upsertDocsTransactionallyReactive(eq(reactiveTransactionAttemptContext),
                anyList())).thenReturn(mockedPublisher);

        Publisher<?> resultPublisher =
                statement.doInTransactionReactive(reactiveTransactionAttemptContext, clusterOperator);
        assertThat(resultPublisher).isEqualTo(mockedPublisher);

        verify(collectionOperator).upsertDocsTransactionallyReactive(eq(reactiveTransactionAttemptContext), anyList());
    }
}