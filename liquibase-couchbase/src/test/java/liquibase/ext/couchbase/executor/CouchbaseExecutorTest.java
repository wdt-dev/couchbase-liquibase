package liquibase.ext.couchbase.executor;

import java.util.Collections;
import java.util.List;

import com.couchbase.client.java.Cluster;
import liquibase.exception.DatabaseException;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.exception.StatementExecutionException;
import liquibase.ext.couchbase.executor.service.TransactionExecutorService;
import liquibase.ext.couchbase.statement.CouchbaseStatement;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;
import liquibase.sql.visitor.SqlVisitor;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CouchbaseExecutorTest {

    private final CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
    private final Cluster cluster = mock(Cluster.class);
    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final TransactionExecutorService transactionExecutorService = mock(TransactionExecutorService.class);

    private CouchbaseExecutor couchbaseExecutor;

    @BeforeEach
    void setUp() {
        couchbaseExecutor = new CouchbaseExecutor();
        couchbaseExecutor.setDatabase(database);

        when(database.getConnection()).thenReturn(connection);
        when(connection.getTransactionExecutorService()).thenReturn(transactionExecutorService);
        when(connection.getCluster()).thenReturn(cluster);
    }

    @Test
    public void Should_support_liquibase() {
        assertThat(couchbaseExecutor.supports(database)).isTrue();
    }

    @Test
    public void Should_throw_if_statement_not_couchbase() {
        SqlStatement sqlStatement = mock(SqlStatement.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> couchbaseExecutor.execute(sqlStatement));
    }

    @Test
    public void Should_add_transaction_statement_to_queue() throws DatabaseException {
        CouchbaseTransactionStatement sqlStatement = mock(CouchbaseTransactionStatement.class);
        couchbaseExecutor.execute(sqlStatement);

        verify(transactionExecutorService).addStatementIntoQueue(sqlStatement);
    }

    @Test
    public void Should_execute_statement() throws DatabaseException {
        CouchbaseStatement sqlStatement = mock(CouchbaseStatement.class);
        couchbaseExecutor.execute(sqlStatement);

        verify(sqlStatement).execute(any());
    }

    @Test
    public void Should_wrap_exception() {
        CouchbaseStatement sqlStatement = mock(CouchbaseStatement.class);

        doThrow(new RuntimeException("Mocked")).when(sqlStatement).execute(any());

        assertThatExceptionOfType(StatementExecutionException.class)
                .isThrownBy(() -> couchbaseExecutor.execute(sqlStatement));
    }
}
