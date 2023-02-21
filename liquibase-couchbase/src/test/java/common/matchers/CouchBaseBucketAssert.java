package common.matchers;

import com.couchbase.client.java.ReactiveBucket;
import liquibase.ext.couchbase.operator.BucketOperator;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

public class CouchBaseBucketAssert extends AbstractAssert<CouchBaseBucketAssert, ReactiveBucket> {

    private final BucketOperator bucketOperator;

    private CouchBaseBucketAssert(ReactiveBucket bucket) {
        super(bucket, CouchBaseBucketAssert.class);
        bucketOperator = new BucketOperator(bucket);
    }


    public static CouchBaseBucketAssert assertThat(@NonNull ReactiveBucket actual) {
        return new CouchBaseBucketAssert(actual);
    }

    public CouchBaseBucketAssert hasCollectionInDefaultScope(@NonNull String collectionName) {
        if (!bucketOperator.hasCollectionInDefaultScope(collectionName)) {
            failWithMessage("Collection <%s> not exists in bucket <%s> in scope <%s>", collectionName, actual.name(),
                    actual.defaultScope().name());
        }

        return this;
    }

    public CouchBaseBucketAssert hasCollectionInScope(@NonNull String collectionName, @NonNull String scopeName) {
        if (!bucketOperator.hasCollectionInScope(collectionName, scopeName)) {
            failWithMessage("Collection <%s> not exists in bucket <%s> in scope <%s>", collectionName, actual.name(),
                    scopeName);
        }

        return this;
    }

    public CouchBaseBucketAssert hasNoCollectionInScope(@NonNull String collectionName, @NonNull String scopeName) {
        if (bucketOperator.hasCollectionInScope(collectionName, scopeName)) {
            failWithMessage("Collection <%s> exists in bucket <%s> in scope <%s>", collectionName, actual.name(),
                    scopeName);
        }

        return this;
    }

}
