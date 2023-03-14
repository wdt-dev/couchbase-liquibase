package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * A statement to drop bucket
 * @see CouchbaseStatement
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class N1qlStatement extends CouchbaseTransactionStatement/* CouchbaseStatement*/ {

    private final List<String> queries;

//     @Override
//     public void execute(ClusterOperator clusterOperator) {
//         clusterOperator.executeN1ql(queries);
//     }

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
//         Map<String, Object> contentList = clusterOperator.checkDocsAndTransformToObjects(documents);
        clusterOperator.executeN1ql(transaction, queries);
    }
}
