package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.transactions.config.TransactionOptions;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.TransactionalReactiveStatementExecutionException;
import liquibase.ext.couchbase.executor.TransactionalReactiveStatementQueue;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;
import liquibase.ext.couchbase.types.CouchbaseReactiveTransactionAction;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static com.couchbase.client.java.transactions.config.TransactionOptions.transactionOptions;
import static java.time.Duration.ofMinutes;

public class ReactiveTransactionExecutorService extends TransactionExecutorService {

    private static final int TRANSACTION_WAIT_TIME_IN_MIN = 20; // TODO to properties and change to seconds
    private static final int REACTIVE_TRANSACTION_PARALLEL_THREADS = 16; // TODO to properties

    private final TransactionalReactiveStatementQueue transactionalReactiveStatementQueue = Scope.getCurrentScope()
            .getSingleton(TransactionalReactiveStatementQueue.class);

    public ReactiveTransactionExecutorService(Cluster cluster) {
        super(cluster);
    }

    @Override
    public void addStatementIntoQueue(CouchbaseTransactionStatement transactionStatement) {
        transactionalReactiveStatementQueue.add(transactionStatement.asTransactionReactiveAction(clusterOperator));
    }

    @Override
    public void executeStatementsInTransaction() {
        if (transactionalReactiveStatementQueue.isEmpty()) {
            return;
        }

        try {
            TransactionOptions options = transactionOptions().timeout(ofMinutes(TRANSACTION_WAIT_TIME_IN_MIN));
            Flux<CouchbaseReactiveTransactionAction> statements = Flux.fromIterable(transactionalReactiveStatementQueue);
            cluster.reactive().transactions()
                    .run(ctx -> statements
                            .parallel(REACTIVE_TRANSACTION_PARALLEL_THREADS)
                            .runOn(Schedulers.boundedElastic())
                            .concatMap(action -> action.apply(ctx))
                            .sequential()
                            .then(), options)
                    .block();
        } catch (TransactionFailedException e) {
            throw new TransactionalReactiveStatementExecutionException(e);
        } finally {
            transactionalReactiveStatementQueue.clear();
        }
    }

    @Override
    public void clearStatementsQueue() {
        transactionalReactiveStatementQueue.clear();
    }
}
