package common.matchers;

import com.couchbase.client.java.Bucket;

import org.assertj.core.api.AbstractAssert;

import liquibase.ext.couchbase.operator.BucketOperator;
import lombok.NonNull;

public class CouchBaseBucketAssert extends AbstractAssert<CouchBaseBucketAssert, Bucket> {

    private final BucketOperator bucketOperator;

    private CouchBaseBucketAssert(Bucket bucket) {
        super(bucket, CouchBaseBucketAssert.class);
        bucketOperator = new BucketOperator(bucket);
    }


    public static CouchBaseBucketAssert assertThat(@NonNull Bucket actual) {
        return new CouchBaseBucketAssert(actual);
    }

    public CouchBaseBucketAssert hasCollectionInDefaultScope(@NonNull String collectionName) {
        if (!bucketOperator.hasCollectionInDefaultScope(collectionName)) {
            failWithMessage("Collection [%s] doesn't exist in the bucket [%s] in the scope [%s]",
                    collectionName,
                    actual.name(),
                    actual.defaultScope().name()
            );
        }

        return this;
    }

    public CouchBaseBucketAssert hasCollectionInScope(@NonNull String collectionName,
                                                      @NonNull String scopeName) {
        if (!bucketOperator.hasCollectionInScope(collectionName, scopeName)) {
            failWithMessage("Collection [%s] doesn't exist in the bucket [%s] in the scope [%s]",
                    collectionName,
                    actual.name(),
                    scopeName
            );
        }

        return this;
    }

    public CouchBaseBucketAssert hasNoCollectionInScope(@NonNull String collectionName,
                                                        @NonNull String scopeName) {
        if (bucketOperator.hasCollectionInScope(collectionName, scopeName)) {
            failWithMessage("Collection [%s] exists in the bucket [%s] in the scope [%s]",
                    collectionName,
                    actual.name(),
                    scopeName
            );
        }

        return this;
    }

}
