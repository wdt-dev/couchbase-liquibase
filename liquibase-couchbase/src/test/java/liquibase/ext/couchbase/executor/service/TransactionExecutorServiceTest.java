package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import org.junit.jupiter.api.Test;

import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.IS_REACTIVE_TRANSACTIONS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class TransactionExecutorServiceTest {

    private final Cluster cluster = mock(Cluster.class);

    @Test
    public void Should_return_PlainTransactionExecutorService_or_ReactiveTransactionExecutorService() {
        TransactionExecutorService transactionExecutorService = TransactionExecutorService.getExecutor(cluster);

        if (IS_REACTIVE_TRANSACTIONS.getCurrentValue()) {
            assertThat(transactionExecutorService).isInstanceOf(ReactiveTransactionExecutorService.class);
        } else {
            assertThat(transactionExecutorService).isInstanceOf(PlainTransactionExecutorService.class);
        }
    }

}
