package liquibase.ext.couchbase.statement;

import com.couchbase.client.core.error.CollectionExistsException;
import liquibase.ext.couchbase.types.Keyspace;

import liquibase.Scope;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

/**
 * A statement to create a collection
 * @see liquibase.ext.couchbase.change.CreateCollectionChange
 * @see liquibase.ext.couchbase.precondition.CollectionExistsPrecondition
 * @see CouchbaseStatement
 * @see Keyspace
 */

@Data
@RequiredArgsConstructor
public class CreateCollectionStatement extends CouchbaseStatement {

    private static final String existsMsg = "Collection %s already exists, skipping creation";
    private final Logger logger = Scope.getCurrentScope().getLog(CreateCollectionStatement.class);

    private final Keyspace keyspace;
    private final boolean skipIfExists;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());
        boolean isExists = bucketOperator.hasCollectionInScope(keyspace.getCollection(), keyspace.getScope());
        if (skipIfExists && isExists) {
            logger.info(format(existsMsg, keyspace.getCollection()));
            return;
        }
        if (isExists) {
            throw new CollectionExistsException(keyspace.getCollection());
        }

        bucketOperator.createCollection(keyspace.getCollection(), keyspace.getScope());
    }
}
