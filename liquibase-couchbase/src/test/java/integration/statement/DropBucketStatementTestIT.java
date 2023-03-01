package integration.statement;

import common.ConstantScopeTestCase;
import liquibase.ext.couchbase.exception.BucketNotExistException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.DropBucketStatement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.CREATE_BUCKET_TEST_NAME;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class DropBucketStatementTestIT extends ConstantScopeTestCase {

    @Test
    void Should_drop_existing_bucket() {
        DropBucketStatement statement = new DropBucketStatement(CREATE_BUCKET_TEST_NAME);
        statement.execute(new ClusterOperator(cluster));

        assertThat(cluster).hasNoBucket(CREATE_BUCKET_TEST_NAME);
        bucketOperator.createDefaultTestCollection();
    }


    @Test
    void Should_throw_error_when_delete_non_existing_bucket() {
        String notFoundBucketName = "notFoundBucket";
        DropBucketStatement statement = new DropBucketStatement(notFoundBucketName);

        assertThatExceptionOfType(BucketNotExistException.class)
                .isThrownBy(() -> statement.execute(new ClusterOperator(cluster)))
                .withMessage("Bucket [%s] not exists", notFoundBucketName);
    }

    @AfterEach
    void tearDown() {
        if (!bucketOperator.hasScope(TEST_SCOPE)) {
            bucketOperator.createDefaultTestScope();
        }
        if (!bucketOperator.hasCollectionInScope(TEST_COLLECTION, TEST_SCOPE)) {
            bucketOperator.createDefaultTestCollection();
        }
    }
}