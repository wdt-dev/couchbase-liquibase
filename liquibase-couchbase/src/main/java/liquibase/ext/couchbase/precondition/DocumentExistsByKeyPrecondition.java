package liquibase.ext.couchbase.precondition;

import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.DocumentNotExistsPreconditionException;
import liquibase.ext.couchbase.statement.DocumentExistsByKeyStatement;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.exception.PreconditionFailedException;
import lombok.Data;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;

/**
 * A precondition that checks if a document exists.
 * @see AbstractCouchbasePrecondition
 * @see liquibase.precondition.AbstractPrecondition
 * @see DocumentNotExistsPreconditionException
 */

@Data
public class DocumentExistsByKeyPrecondition extends AbstractCouchbasePrecondition {

    private String bucketName;
    private String scopeName;
    private String collectionName;
    private String key;

    @Override
    public String getName() {
        return "documentExists";
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws PreconditionFailedException {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        final DocumentExistsByKeyStatement documentExistsByKeyStatement = new DocumentExistsByKeyStatement(keyspace, key);

        if (documentExistsByKeyStatement.isTrue((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new DocumentNotExistsPreconditionException(key, bucketName, scopeName, collectionName, changeLog, this);
    }

}
