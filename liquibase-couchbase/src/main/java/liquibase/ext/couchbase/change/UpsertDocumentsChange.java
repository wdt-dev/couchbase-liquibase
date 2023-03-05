package liquibase.ext.couchbase.change;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.InsertDocumentsStatement;
import liquibase.ext.couchbase.statement.UpsertDocumentsStatement;
import liquibase.ext.couchbase.statement.UpsertFromFileStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;

/**
 * Part of change set package. Responsible for upserting multiple documents into a collection.
 * @link <a href="https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html#upsert">Reference documentation</a>
 * @see UpsertDocumentsStatement
 * @see Keyspace
 */

@Getter
@Setter
@DatabaseChange(
        name = "upsertDocuments",
        description = "Upserts multiple documents into a collection https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
@AllArgsConstructor
public class UpsertDocumentsChange extends CouchbaseChange {

    private String bucketName;
    private String scopeName;
    private String collectionName;
    private List<Document> documents = new ArrayList<>();
    private File file = new File();

    @Override
    public String getConfirmationMessage() {
        return String.format("Documents upserted into collection %s", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        Optional<String> hasFile = Optional.ofNullable(file).map(File::getFilePath).filter(StringUtils::isNotBlank);
        return new SqlStatement[] {
                hasFile.isPresent() ? new UpsertFromFileStatement(keyspace, file) :
                        new InsertDocumentsStatement(keyspace, documents)
        };
    }
}

