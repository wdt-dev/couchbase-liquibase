package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Id;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.reactivestreams.Publisher;

@Getter
@EqualsAndHashCode(callSuper = true)
public class RemoveDocumentsSqlPlusPlusQueryStatement extends RemoveDocumentsStatement {

    private final String sqlPlusPlusQuery;

    public RemoveDocumentsSqlPlusPlusQueryStatement(Keyspace keyspace, Set<Id> ids, String sqlPlusPlusQuery) {
        super(keyspace, ids);
        this.sqlPlusPlusQuery = sqlPlusPlusQuery;
    }

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        List<String> filteredIds = clusterOperator.retrieveDocumentIdsBySqlPlusPlusQuery(sqlPlusPlusQuery);
        getIds().addAll(filteredIds.stream().map(Id::new).collect(Collectors.toList()));
        super.doInTransaction(transaction, clusterOperator);
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        List<String> filteredIds = clusterOperator.retrieveDocumentIdsBySqlPlusPlusQuery(sqlPlusPlusQuery);
        getIds().addAll(filteredIds.stream().map(Id::new).collect(Collectors.toList()));
        return super.doInTransactionReactive(transaction, clusterOperator);
    }

}
