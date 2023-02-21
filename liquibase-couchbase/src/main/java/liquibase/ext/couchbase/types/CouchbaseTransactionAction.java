package liquibase.ext.couchbase.types;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;

import java.util.function.Consumer;

public interface CouchbaseTransactionAction extends Consumer<ReactiveTransactionAttemptContext> {}
