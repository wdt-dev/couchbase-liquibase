package integration.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import com.google.common.collect.Lists;
import common.TransactionStatementTest;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.RemoveDocumentsStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Id;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class RemoveDocumentsStatementIT extends TransactionStatementTest {
    private final TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
    private List<Id> ids;
    private Collection collection;
    private Keyspace keyspace;

    @BeforeEach
    void setUp() {
        collection = bucketOperator.getCollection(collectionName, scopeName);
        keyspace = keyspace(bucketName, scopeName, collectionName);
        Document doc1 = collectionOperator.generateTestDoc();
        Document doc2 = collectionOperator.generateTestDoc();
        ids = Lists.newArrayList(new Id(doc1.getId()), new Id(doc2.getId()));
        collectionOperator.insertDoc(doc1);
        collectionOperator.insertDoc(doc2);
    }

    @Test
    void Should_insert_and_update_many_documents() {
        RemoveDocumentsStatement statement = new RemoveDocumentsStatement(keyspace, ids);

        doInTransaction(statement.asTransactionAction(clusterOperator));

        assertThat(collection).doesNotContainIds(ids);
    }

    @Test
    void Should_not_remove_documents_when_transaction_was_broken() {
        RemoveDocumentsStatement statement = new RemoveDocumentsStatement(keyspace, ids);

        assertThatExceptionOfType(TransactionFailedException.class)
                .isThrownBy(() -> doInFailingTransaction(statement.asTransactionAction(clusterOperator)));

        assertThat(collection).containsAnyId(ids);
    }

}
