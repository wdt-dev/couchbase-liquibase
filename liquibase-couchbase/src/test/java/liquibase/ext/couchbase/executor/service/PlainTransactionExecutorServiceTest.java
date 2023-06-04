package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.transactions.Transactions;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.ext.couchbase.exception.TransactionalStatementExecutionException;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlainTransactionExecutorServiceTest {
    private final Cluster cluster = mock(Cluster.class);
    private final Transactions transactions = mock(Transactions.class);

    private PlainTransactionExecutorService plainTransactionExecutorService;

    @BeforeEach
    public void setUp() {
        plainTransactionExecutorService = new PlainTransactionExecutorService(cluster);
        when(cluster.transactions()).thenReturn(transactions);
    }

    @Test
    public void Should_not_execute_empty() {
        when(cluster.transactions()).thenThrow(new UnsupportedOperationException("Mocked"));

        plainTransactionExecutorService.executeStatementsInTransaction();

        verify(cluster, never()).transactions();
    }

    @Test
    public void Should_clear_successfully() {
        plainTransactionExecutorService.addStatementIntoQueue(mock(CouchbaseTransactionStatement.class));

        when(cluster.transactions()).thenThrow(new UnsupportedOperationException("Mocked"));

        plainTransactionExecutorService.clearStatementsQueue();

        plainTransactionExecutorService.executeStatementsInTransaction();

        verify(cluster, never()).transactions();
    }

    @Test
    public void Should_execute_successfully() {
        plainTransactionExecutorService.addStatementIntoQueue(mock(CouchbaseTransactionStatement.class));

        plainTransactionExecutorService.executeStatementsInTransaction();

        verify(cluster).transactions();
        verify(transactions).run(any(), any());
    }

    @Test
    public void Should_catch_TransactionalStatementExecutionException() {
        plainTransactionExecutorService.addStatementIntoQueue(mock(CouchbaseTransactionStatement.class));

        TransactionFailedException mockedException = mock(TransactionFailedException.class);
        when(transactions.run(any(), any())).thenThrow(mockedException);

        assertThatExceptionOfType(TransactionalStatementExecutionException.class)
                .isThrownBy(() -> plainTransactionExecutorService.executeStatementsInTransaction());

        verify(cluster).transactions();
        verify(transactions).run(any(), any());
    }

}
