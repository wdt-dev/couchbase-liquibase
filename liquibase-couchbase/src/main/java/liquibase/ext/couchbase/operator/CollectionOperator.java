package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Common facade on {@link Bucket} including all common operations <br > and state checks
 */
@RequiredArgsConstructor
public class CollectionOperator {

    @Getter
    protected final ReactiveCollection collection;

    public void insertDoc(String id, JsonObject content) {
        collection.insert(id, content);
    }

    public void insertDocInTransaction(ReactiveTransactionAttemptContext transaction,
                                                             String id,
                                                             JsonObject content) {
        transaction.insert(collection, id, content);
    }

    public boolean docExists(String id) {
        return Objects.requireNonNull(collection.exists(id).block()).exists();
    }

    public void removeDoc(String id) {
        collection.remove(id);
    }

    public void removeDocs(String... ids) {
        Arrays.stream(ids).forEach(collection::remove);
    }

    public void upsertDoc(String id, JsonObject content) {
        collection.upsert(id, content);
    }

    private void upsertDocInTransaction(ReactiveTransactionAttemptContext transaction,
                                        String key,
                                        JsonObject jsonObject) {
        try {
            Mono<TransactionGetResult> document = transaction.get(collection, key);
            transaction.replace((TransactionGetResult) document.subscribe(), jsonObject);
        }
        catch (DocumentNotFoundException ex) {
            transaction.insert(collection, key, jsonObject);
        }
    }

    public void upsertDocs(Map<String, JsonObject> docs) {
        docs.forEach(this::upsertDoc);
    }

    public void upsertDocsTransactionally(ReactiveTransactionAttemptContext context, Map<String, JsonObject> docs) {
        docs.forEach((key, jsonObject) -> upsertDocInTransaction(context, key, jsonObject));
    }

    public void insertDocs(Map<String, JsonObject> docs) {
        docs.forEach(this::insertDoc);
    }

    public void insertDocsTransactionally(ReactiveTransactionAttemptContext context, Map<String, JsonObject> docs) {
        docs.forEach((key, jsonObject) -> insertDocInTransaction(context, key, jsonObject));
    }

}
