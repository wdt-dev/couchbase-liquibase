package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.transactions.config.TransactionOptions;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.TransactionalStatementExecutionException;
import liquibase.ext.couchbase.executor.TransactionalStatementQueue;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;

import static com.couchbase.client.java.transactions.config.TransactionOptions.transactionOptions;
import static java.time.Duration.ofMinutes;

public class PlainTransactionExecutorService extends TransactionExecutorService {

    private static final int TRANSACTION_WAIT_TIME_IN_MIN = 20; // TODO to properties and change to seconds
    private final TransactionalStatementQueue transactionalStatementQueue = Scope.getCurrentScope()
            .getSingleton(TransactionalStatementQueue.class);

    public PlainTransactionExecutorService(Cluster cluster) {
        super(cluster);
    }

    @Override
    public void addStatementIntoQueue(CouchbaseTransactionStatement transactionStatement) {
        transactionalStatementQueue.add(transactionStatement.asTransactionAction(clusterOperator));
    }

    @Override
    public void executeStatementsInTransaction() {
        if (transactionalStatementQueue.isEmpty()) {
            return;
        }

        try {
            TransactionOptions options = transactionOptions().timeout(ofMinutes(TRANSACTION_WAIT_TIME_IN_MIN));
            cluster.transactions()
                    .run(ctx -> transactionalStatementQueue.forEach(it -> it.accept(ctx)), options);
        } catch (TransactionFailedException e) {
            throw new TransactionalStatementExecutionException(e);
        } finally {
            transactionalStatementQueue.clear();
        }
    }

    @Override
    public void clearStatementsQueue() {
        transactionalStatementQueue.clear();
    }
}
