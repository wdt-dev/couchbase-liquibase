package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import liquibase.ext.couchbase.types.Document;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;

/**
 * Common facade on {@link Bucket} including all common operations <br > and state checks
 */
@RequiredArgsConstructor
public class CollectionOperator {

    @Getter
    protected final Collection collection;

    public void insertDoc(String id, JsonObject content) {
        collection.insert(id, content);
    }

    public void insertDocInTransaction(TransactionAttemptContext transaction, String id, Object content) {
        transaction.insert(collection, id, content);
    }

    //TODO check later with single invocation for each element with .then() in the end
    public Mono<TransactionGetResult> insertDocInTransactionReactive(ReactiveTransactionAttemptContext transaction, String id, JsonObject content) {
        return transaction.insert(collection.reactive(), id, content);
    }

    public void insertDoc(Document document) {
        collection.insert(document.getId(), document.getValue().mapDataToType());
    }

    public boolean docExists(String id) {
        return collection.exists(id).exists();
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

    private void upsertDocInTransaction(TransactionAttemptContext transaction,
                                        String key,
                                        Object content) {
        try {
            TransactionGetResult document = transaction.get(collection, key);
            transaction.replace(document, content);
        } catch (DocumentNotFoundException ex) {
            transaction.insert(collection, key, content);
        }
    }

    //TODO check later with single invocation for each element with .then() in the end
    public Mono<TransactionGetResult> upsertDocInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                     String key,
                                                     JsonObject jsonObject) {
        Mono<TransactionGetResult> document = transaction.get(collection.reactive(), key);
        return document.doOnNext(transactionGetResult -> transaction.replace(transactionGetResult, jsonObject))
                .onErrorResume(DocumentNotFoundException.class::isInstance,
                        throwable -> transaction.insert(collection.reactive(), key, jsonObject));
    }

    public void upsertDocs(Map<String, JsonObject> docs) {
        docs.forEach(this::upsertDoc);
    }

    public void upsertDocsTransactionally(TransactionAttemptContext transaction, Map<String, JsonObject> docs) {
        docs.forEach((key, jsonObject) -> upsertDocInTransaction(transaction, key, jsonObject));
    }

    public void upsertDocsTransactionally(TransactionAttemptContext transaction, Map<String, Object> docs) {
        docs.forEach((key, content) -> upsertDocInTransaction(transaction, key, content));
    }


    public Flux<TransactionGetResult> upsertDocsTransactionallyReactive(ReactiveTransactionAttemptContext transaction, Map<String, JsonObject> docs) {
        return Flux.fromIterable(docs.entrySet())
                .flatMap(entry -> upsertDocInTransactionReactive(transaction, entry.getKey(), entry.getValue()));
    }

    public void insertDocsTransactionally(TransactionAttemptContext transaction, Map<String, Object> docs) {
        docs.forEach((key, content) -> insertDocInTransaction(transaction, key, content));
    }

    public Flux<TransactionGetResult> insertDocsTransactionallyReactive(ReactiveTransactionAttemptContext transaction, Map<String, JsonObject> docs) {
        return Flux.fromIterable(docs.entrySet())
                .flatMap(entry -> insertDocInTransactionReactive(transaction, entry.getKey(), entry.getValue()));
    }
}
