package integration.statement;

import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.CollectionExistsStatement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionExistsStatementIT extends RandomizedScopeTestCase {
    @Test
    void Should_return_true_when_collection_exists() {
        CollectionExistsStatement statement = new CollectionExistsStatement(bucketName, scopeName,
                collectionName);

        assertThat(statement.isTrue(database.getConnection())).isTrue();
    }

    @Test
    void Should_return_false_when_collection_does_not_exist() {
        CollectionExistsStatement statement = new CollectionExistsStatement(bucketName, scopeName,
                "notCreatedCollection");

        assertThat(statement.isTrue(database.getConnection())).isFalse();
    }
}
