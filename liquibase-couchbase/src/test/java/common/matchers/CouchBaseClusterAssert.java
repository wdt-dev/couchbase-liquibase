package common.matchers;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;

import org.assertj.core.api.AbstractAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import liquibase.ext.couchbase.types.Keyspace;
import lombok.NonNull;

public class CouchBaseClusterAssert extends AbstractAssert<CouchBaseClusterAssert, Cluster> {

    private CouchBaseClusterAssert(Cluster cluster) {
        super(cluster, CouchBaseClusterAssert.class);
    }

    public static CouchBaseClusterAssert assertThat(@NonNull Cluster actual) {
        return new CouchBaseClusterAssert(actual);
    }

    /**
     * Query indexes for default scope
     */
    public QueryIndexAssert queryIndexes(String bucketName) {
        QueryIndexManager queryIndexManager = actual.queryIndexes();
        List<QueryIndex> indexes = queryIndexManager.getAllIndexes(bucketName);
        return new QueryIndexAssert(queryIndexManager, indexes, bucketName);
    }

    /**
     * Query indexes for specific Keyspace {@link Keyspace}
     */
    public QueryIndexAssert queryIndexes(Keyspace keyspace) {
        QueryIndexManager queryIndexManager = actual.queryIndexes();
        List<QueryIndex> indexes = queryIndexManager.getAllIndexes(keyspace.getBucket());
        return new QueryIndexAssert(queryIndexManager, indexes, keyspace.getBucket());
    }

    public CouchBaseClusterAssert hasBucket(String bucketName) {
        BucketSettings bucket = actual.buckets().getBucket(bucketName);
        if (bucket == null) {
            failWithMessage("Bucket [%s] doesn't exist", bucketName);
        }
        return this;
    }

    public CouchBaseClusterAssert hasNoBucket(String bucketName) {
        Map<String, BucketSettings> allBuckets = actual.buckets().getAllBuckets();
        if (allBuckets.containsKey(bucketName)) {
            failWithMessage("Failed to delete bucket [%s]", bucketName);
        }
        return this;
    }

    public CouchBaseClusterAssert bucketUpdatedSuccessfully(String bucketName, BucketSettings settings) {
        BucketSettings actualSettings = actual.buckets().getBucket(bucketName);
        List<String> invalidFields = new ArrayList<>();
        if (settings.flushEnabled() != actualSettings.flushEnabled()) {
            invalidFields.add("flushEnabled");
        }
        if (settings.compressionMode() != actualSettings.compressionMode()) {
            invalidFields.add("compressionMode");
        }
        if (!settings.maxExpiry().equals(actualSettings.maxExpiry())) {
            invalidFields.add("maxExpiry");
        }
        if (settings.numReplicas() != actualSettings.numReplicas()) {
            invalidFields.add("numReplicas");
        }
        if (settings.ramQuotaMB() != actualSettings.ramQuotaMB()) {
            invalidFields.add("ramQuotaMB");
        }
        if (!invalidFields.isEmpty()) {
            failWithMessage("The <%s> properties of the bucket has not been updated", invalidFields);
        }

        return this;
    }
}
