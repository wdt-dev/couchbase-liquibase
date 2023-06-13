package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Id;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.reactivestreams.Publisher;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Getter
@EqualsAndHashCode(callSuper = true)
public class RemoveDocumentsSqlQueryStatement extends RemoveDocumentsStatement {

    private final String sqlPlusPlusQuery;

    public RemoveDocumentsSqlQueryStatement(Keyspace keyspace, Set<Id> ids, String sqlPlusPlusQuery) {
        super(keyspace, ids);
        this.sqlPlusPlusQuery = sqlPlusPlusQuery;
    }

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        initSelectIds(clusterOperator);
        super.doInTransaction(transaction, clusterOperator);
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        initSelectIds(clusterOperator);
        return super.doInTransactionReactive(transaction, clusterOperator);
    }

    private void initSelectIds(ClusterOperator clusterOperator) {
        List<String> filteredIds = clusterOperator.retrieveDocumentIdsBySqlPlusPlusQuery(sqlPlusPlusQuery);
        getIds().addAll(filteredIds.stream().map(Id::new).collect(toList()));
    }

}
