package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A statement to insert documents from file inside one transaction into a keyspace
 * @see Document
 * @see CouchbaseStatement
 * @see Keyspace
 */

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InsertFromFileStatement extends CouchbaseFromFileStatement {
    private final Keyspace keyspace;
    private final File file;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        CollectionOperator collectionOperator = clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope());
        importFromFileWith(transaction, file, clusterOperator, collectionOperator::upsertDocsTransactionally);
    }
}

