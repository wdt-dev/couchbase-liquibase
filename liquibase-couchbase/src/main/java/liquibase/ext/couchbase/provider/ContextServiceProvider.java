package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.ReactiveBucket;
import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.ReactiveScope;
import com.couchbase.client.java.manager.bucket.ReactiveBucketManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Optional;

import static com.couchbase.client.java.manager.bucket.BucketSettings.create;

/**
 * A concrete implementation of {@link ServiceProvider} interface. Uses either default bucket from
 * {@link CouchbaseLiquibaseDatabase} or creates a new one as a fallback option.<br><br>
 */

@RequiredArgsConstructor
public class ContextServiceProvider implements ServiceProvider {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private final CouchbaseLiquibaseDatabase database;

    @Override
    public ReactiveCollection getServiceCollection(String collectionName) {
        CouchbaseConnection connection = database.getConnection();
        ReactiveBucket serviceBucket = Optional.ofNullable(connection.getDatabase()).orElseGet(
                () -> getServiceBucketFrom(connection.getCluster()));
        checkAndCreateScopeAndCollectionIn(serviceBucket, collectionName);
        return serviceBucket.scope(DEFAULT_SERVICE_SCOPE).collection(collectionName);
    }

    private void checkAndCreateScopeAndCollectionIn(ReactiveBucket bucket, String collectionName) {
        if (!serviceScopeExistsIn(bucket)) {
            bucket.collections().createScope(DEFAULT_SERVICE_SCOPE);
            bucket.waitUntilReady(TIMEOUT);
        }
        if (!collectionExistsIn(bucket, collectionName)) {
            bucket.collections().createCollection(CollectionSpec.create(collectionName, DEFAULT_SERVICE_SCOPE));
            bucket.waitUntilReady(TIMEOUT);
        }
    }

    private ReactiveBucket getServiceBucketFrom(ReactiveCluster cluster) {
        ReactiveBucketManager manager = cluster.buckets();
        boolean serviceBucketExists = manager.getAllBuckets()
                .block().values().stream().anyMatch(
                bucketSettings -> bucketSettings.name().equals(FALLBACK_SERVICE_BUCKET_NAME));
        if (!serviceBucketExists) {
            manager.createBucket(create(FALLBACK_SERVICE_BUCKET_NAME));
            cluster.waitUntilReady(TIMEOUT);
        }
        return cluster.bucket(FALLBACK_SERVICE_BUCKET_NAME);
    }

    private boolean serviceScopeExistsIn(ReactiveBucket bucket) {
        return bucket.collections().getAllScopes().toStream().anyMatch(
                scope -> scope.name().equals(DEFAULT_SERVICE_SCOPE));
    }

    private boolean collectionExistsIn(ReactiveBucket bucket, String collectionName) {
        return bucket.collections().getAllScopes().toStream().map(ScopeSpec::collections).flatMap(
                java.util.Collection::stream).map(CollectionSpec::name).anyMatch(collectionName::equals);
    }

    @Override
    public ReactiveScope getScopeOfCollection(String collectionName) {
        ReactiveCollection serviceCollection = getServiceCollection(collectionName);
        return database.getConnection().getCluster().bucket(serviceCollection.bucketName()).scope(
                serviceCollection.scopeName());
    }

    @Override
    public String getServiceBucketName() {
        ReactiveBucket serviceBucket = database.getConnection().getDatabase();
        return Optional.ofNullable(serviceBucket).map(ReactiveBucket::name).orElse(FALLBACK_SERVICE_BUCKET_NAME);
    }

}
