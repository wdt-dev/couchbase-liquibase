package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TransactionExecutorService {

    protected final Cluster cluster;
    protected final ClusterOperator clusterOperator = new ClusterOperator(cluster);

    abstract public void addStatementIntoQueue(CouchbaseTransactionStatement transactionStatement);
    abstract public void executeStatementsInTransaction();
    abstract public void clearStatementsQueue();

}
