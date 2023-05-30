package org.liquibase.ext.couchbase.starter.test;

import com.couchbase.client.java.Collection;
import liquibase.ext.couchbase.operator.CollectionOperator;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.starter.common.SpringBootCouchbaseContainerizedTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InsertDocumentsSystemTest extends SpringBootCouchbaseContainerizedTest {

    private static final Collection collection = cluster.bucket(TEST_BUCKET).scope(TEST_SCOPE).collection(TEST_COLLECTION);
    private static final CollectionOperator collectionOperator = new CollectionOperator(collection);

    @DynamicPropertySource
    static void overrideUrlProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.couchbase.change-log", () -> "classpath:/testdb/changelog/insert-document.xml");
    }

    @Test
    public void Should_insert_new_document() {
        assertTrue(collectionOperator.docExists("insertId1"));
        collection.remove("insertId1");
    }
}
