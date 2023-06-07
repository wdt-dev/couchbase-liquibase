package liquibase.ext.couchbase.provider;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static liquibase.ext.couchbase.provider.ServiceProvider.SERVICE_BUCKET_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContextServiceProviderTest {

    private final CouchbaseLiquibaseDatabase couchbaseLiquibaseDatabase = mock(CouchbaseLiquibaseDatabase.class);
    private final CouchbaseConnection couchbaseConnection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final Bucket bucket = mock(Bucket.class);
    private final Scope scope = mock(Scope.class);
    private final Collection collection = mock(Collection.class);
    private final BucketManager bucketManager = mock(BucketManager.class);
    private final CollectionManager collectionManager = mock(CollectionManager.class);
    private final QueryIndexManager queryIndexManager = mock(QueryIndexManager.class);

    @BeforeEach
    void setUp() {
        when(couchbaseLiquibaseDatabase.getConnection()).thenReturn(couchbaseConnection);
        when(couchbaseConnection.getCluster()).thenReturn(cluster);
        when(bucket.collections()).thenReturn(collectionManager);
        when(cluster.queryIndexes()).thenReturn(queryIndexManager);
        when(collection.bucketName()).thenReturn(SERVICE_BUCKET_NAME);
        when(collection.scopeName()).thenReturn(DEFAULT_SERVICE_SCOPE);
    }

    @Test
    void Should_return_scope_of_collection() {
        ContextServiceProvider contextServiceProvider = new ContextServiceProvider(couchbaseLiquibaseDatabase);

        String collectionName = "collection";

        List<ScopeSpec> scopeSpecList = createScopeSpecs(collectionName, DEFAULT_SERVICE_SCOPE);

        List<QueryIndex> queryIndexList = createQueryIndexes(collectionName);
        when(couchbaseConnection.getDatabase()).thenReturn(bucket);
        when(bucket.name()).thenReturn(SERVICE_BUCKET_NAME);
        when(cluster.bucket(SERVICE_BUCKET_NAME)).thenReturn(bucket);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(bucket.scope(DEFAULT_SERVICE_SCOPE)).thenReturn(scope);
        when(scope.collection(collectionName)).thenReturn(collection);
        when(collectionManager.getAllScopes()).thenReturn(scopeSpecList);
        when(queryIndexManager.getAllIndexes(eq(SERVICE_BUCKET_NAME), any())).thenReturn(queryIndexList);

        Scope result = contextServiceProvider.getScopeOfCollection(collectionName);
        assertThat(result).isEqualTo(scope);
    }

    @Test
    void Should_return_service_collection_if_bucket_and_scope_and_collection_exist() {
        ContextServiceProvider contextServiceProvider = new ContextServiceProvider(couchbaseLiquibaseDatabase);

        String collectionName = "collection";

        List<ScopeSpec> scopeSpecList = createScopeSpecs(collectionName, DEFAULT_SERVICE_SCOPE);

        List<QueryIndex> queryIndexList = createQueryIndexes(collectionName);
        when(couchbaseConnection.getDatabase()).thenReturn(bucket);
        when(bucket.name()).thenReturn(SERVICE_BUCKET_NAME);
        when(cluster.bucket(SERVICE_BUCKET_NAME)).thenReturn(bucket);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(bucket.scope(DEFAULT_SERVICE_SCOPE)).thenReturn(scope);
        when(scope.collection(collectionName)).thenReturn(collection);
        when(collectionManager.getAllScopes()).thenReturn(scopeSpecList);
        when(queryIndexManager.getAllIndexes(eq(SERVICE_BUCKET_NAME), any())).thenReturn(queryIndexList);

        Collection result = contextServiceProvider.getServiceCollection(collectionName);
        assertThat(result).isEqualTo(collection);

        verify(bucket).scope(DEFAULT_SERVICE_SCOPE);
        verify(scope).collection(collectionName);

        verify(bucketManager, never()).createBucket(any());
        verify(collectionManager, never()).createCollection(any());
        verify(collectionManager, never()).createScope(any());
        verify(queryIndexManager, never()).createPrimaryIndex(any(), any());
    }

    @Test
    void Should_return_service_collection_if_bucket_and_scope_and_collection_exist_but_database_missing() {
        ContextServiceProvider contextServiceProvider = new ContextServiceProvider(couchbaseLiquibaseDatabase);

        String collectionName = "collection";

        List<ScopeSpec> scopeSpecList = createScopeSpecs(collectionName, DEFAULT_SERVICE_SCOPE);

        List<QueryIndex> queryIndexList = createQueryIndexes(collectionName);
        when(bucket.name()).thenReturn(SERVICE_BUCKET_NAME);
        when(cluster.bucket(SERVICE_BUCKET_NAME)).thenReturn(bucket);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(bucket.scope(DEFAULT_SERVICE_SCOPE)).thenReturn(scope);
        when(scope.collection(collectionName)).thenReturn(collection);
        when(collectionManager.getAllScopes()).thenReturn(scopeSpecList);
        when(queryIndexManager.getAllIndexes(eq(SERVICE_BUCKET_NAME), any())).thenReturn(queryIndexList);

        Collection result = contextServiceProvider.getServiceCollection(collectionName);
        assertThat(result).isEqualTo(collection);

        verify(cluster).bucket(SERVICE_BUCKET_NAME);
        verify(bucket).scope(DEFAULT_SERVICE_SCOPE);
        verify(scope).collection(collectionName);

        verify(bucketManager, never()).createBucket(any());
        verify(collectionManager, never()).createCollection(any());
        verify(collectionManager, never()).createScope(any());
        verify(queryIndexManager, never()).createPrimaryIndex(any(), any());
    }

    @Test
    void Should_return_service_collection_if_bucket_and_scope_and_collection_exist_but_bucket_does_not_exist() {
        ContextServiceProvider contextServiceProvider = new ContextServiceProvider(couchbaseLiquibaseDatabase);

        String collectionName = "collection";

        List<ScopeSpec> scopeSpecList = createScopeSpecs(collectionName, DEFAULT_SERVICE_SCOPE);

        List<QueryIndex> queryIndexList = createQueryIndexes(collectionName);
        when(bucket.name()).thenReturn(SERVICE_BUCKET_NAME);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(bucket.scope(DEFAULT_SERVICE_SCOPE)).thenReturn(scope);
        when(scope.collection(collectionName)).thenReturn(collection);
        when(collectionManager.getAllScopes()).thenReturn(scopeSpecList);
        when(queryIndexManager.getAllIndexes(eq(SERVICE_BUCKET_NAME), any())).thenReturn(queryIndexList);
        when(bucketManager.getBucket(any())).thenThrow(new BucketNotFoundException(""));
        AtomicBoolean isCreated = new AtomicBoolean(false);
        doAnswer((args) -> {
            isCreated.set(true);
            return null;
        }).when(bucketManager).createBucket(any(), any());
        when(cluster.bucket(SERVICE_BUCKET_NAME)).thenAnswer(args -> {
            if (isCreated.get()) {
                return bucket;
            }
            else {
                return null;
            }
        });

        Collection result = contextServiceProvider.getServiceCollection(collectionName);
        assertThat(result).isEqualTo(collection);

        verify(cluster).bucket(SERVICE_BUCKET_NAME);
        verify(bucket).scope(DEFAULT_SERVICE_SCOPE);
        verify(scope).collection(collectionName);

        verify(bucketManager).createBucket(argThat(it -> Objects.equals(it.name(), SERVICE_BUCKET_NAME)), any());

        verify(collectionManager, never()).createCollection(any());
        verify(collectionManager, never()).createScope(any());
        verify(queryIndexManager, never()).createPrimaryIndex(any(), any());
    }

    @Test
    void Should_return_service_collection_if_bucket_exists_but_scope_and_collection_do_not_exist() {
        ContextServiceProvider contextServiceProvider = new ContextServiceProvider(couchbaseLiquibaseDatabase);

        String collectionName = "collection";

        List<QueryIndex> queryIndexList = createQueryIndexes(collectionName);
        when(couchbaseConnection.getDatabase()).thenReturn(bucket);
        when(bucket.name()).thenReturn(SERVICE_BUCKET_NAME);
        when(cluster.bucket(SERVICE_BUCKET_NAME)).thenReturn(bucket);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(bucket.scope(DEFAULT_SERVICE_SCOPE)).thenReturn(scope);
        when(scope.collection(collectionName)).thenReturn(collection);
        when(collectionManager.getAllScopes()).thenReturn(new ArrayList<>());
        when(queryIndexManager.getAllIndexes(eq(SERVICE_BUCKET_NAME), any())).thenReturn(queryIndexList);

        Collection result = contextServiceProvider.getServiceCollection(collectionName);
        assertThat(result).isEqualTo(collection);

        verify(bucket).scope(DEFAULT_SERVICE_SCOPE);
        verify(scope).collection(collectionName);

        verify(collectionManager).createCollection(argThat(it -> Objects.equals(it.name(), collectionName)));
        verify(collectionManager).createScope(DEFAULT_SERVICE_SCOPE);

        verify(bucketManager, never()).createBucket(any(), any());
        verify(queryIndexManager, never()).createPrimaryIndex(any(), any());
    }

    @Test
    void Should_create_index_if_does_not_exist() {
        ContextServiceProvider contextServiceProvider = new ContextServiceProvider(couchbaseLiquibaseDatabase);

        String collectionName = "collection";

        List<ScopeSpec> scopeSpecList = createScopeSpecs(collectionName, DEFAULT_SERVICE_SCOPE);

        when(couchbaseConnection.getDatabase()).thenReturn(bucket);
        when(bucket.name()).thenReturn(SERVICE_BUCKET_NAME);
        when(cluster.bucket(SERVICE_BUCKET_NAME)).thenReturn(bucket);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(bucket.scope(DEFAULT_SERVICE_SCOPE)).thenReturn(scope);
        when(scope.collection(collectionName)).thenReturn(collection);
        when(collectionManager.getAllScopes()).thenReturn(scopeSpecList);
        when(queryIndexManager.getAllIndexes(eq(SERVICE_BUCKET_NAME), any())).thenReturn(new ArrayList<>());

        Collection result = contextServiceProvider.getServiceCollection(collectionName);
        assertThat(result).isEqualTo(collection);

        verify(bucket).scope(DEFAULT_SERVICE_SCOPE);
        verify(scope).collection(collectionName);

        verify(bucketManager, never()).createBucket(any());
        verify(collectionManager, never()).createCollection(any());
        verify(collectionManager, never()).createScope(any());

        verify(queryIndexManager).createPrimaryIndex(eq(SERVICE_BUCKET_NAME), any());
        verify(queryIndexManager).watchIndexes(eq(SERVICE_BUCKET_NAME), any(), any(), any());
    }

    private List<ScopeSpec> createScopeSpecs(String collectionName, String... specNames) {
        List<ScopeSpec> scopeSpecList = new ArrayList<>();
        if (specNames != null) {
            for (String specName : specNames) {
                ScopeSpec scopeSpec = mock(ScopeSpec.class);
                scopeSpecList.add(scopeSpec);

                Set<CollectionSpec> collectionSpecSet = createCollectionSpecs(specName, collectionName);

                when(scopeSpec.name()).thenReturn(specName);
                when(scopeSpec.collections()).thenReturn(collectionSpecSet);
            }
        }
        return scopeSpecList;
    }

    private Set<CollectionSpec> createCollectionSpecs(String scopeName, String... collectionNames) {
        Set<CollectionSpec> collectionSpecList = new HashSet<>();
        if (collectionNames != null) {
            for (String collectionName : collectionNames) {
                CollectionSpec collectionSpec = mock(CollectionSpec.class);
                when(collectionSpec.scopeName()).thenReturn(scopeName);
                when(collectionSpec.name()).thenReturn(collectionName);
                collectionSpecList.add(collectionSpec);
            }
        }
        return collectionSpecList;
    }

    private List<QueryIndex> createQueryIndexes(String... queryIndexes) {
        List<QueryIndex> queryIndexList = new ArrayList<>();
        if (queryIndexes != null) {
            for (String queryIndexName : queryIndexes) {
                QueryIndex queryIndex = mock(QueryIndex.class);
                when(queryIndex.keyspace()).thenReturn(queryIndexName);
                when(queryIndex.primary()).thenReturn(true);
                queryIndexList.add(queryIndex);
            }
        }
        return queryIndexList;
    }


}
