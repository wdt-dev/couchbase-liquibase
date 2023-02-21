package common.matchers;

import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.ReactiveQueryIndexManager;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CouchBaseClusterAssert extends AbstractAssert<CouchBaseClusterAssert, ReactiveCluster> {

    private CouchBaseClusterAssert(ReactiveCluster cluster) {
        super(cluster, CouchBaseClusterAssert.class);
    }

    public static CouchBaseClusterAssert assertThat(@NonNull ReactiveCluster actual) {
        return new CouchBaseClusterAssert(actual);
    }

    /**
     * Query indexes for default scope
     */
    public QueryIndexAssert queryIndexes(String bucketName) {
        ReactiveQueryIndexManager queryIndexManager = actual.queryIndexes();
        Flux<QueryIndex> indexes = queryIndexManager.getAllIndexes(bucketName);
        return new QueryIndexAssert(queryIndexManager, indexes, bucketName);
    }

    /**
     * Query indexes for specific Keyspace {@link Keyspace}
     */
    public QueryIndexAssert queryIndexes(Keyspace keyspace) {
        ReactiveQueryIndexManager queryIndexManager = actual.queryIndexes();
        Flux<QueryIndex> indexes = queryIndexManager.getAllIndexes(keyspace.getBucket());
        return new QueryIndexAssert(queryIndexManager, indexes, keyspace.getBucket());
    }

    public CouchBaseClusterAssert hasBucket(String bucketName) {
        Mono<BucketSettings> bucket = actual.buckets().getBucket(bucketName);
        if (bucket == null) {
            failWithMessage("Bucket with name <%s> does not exist", bucketName);
        }
        return this;
    }

}
