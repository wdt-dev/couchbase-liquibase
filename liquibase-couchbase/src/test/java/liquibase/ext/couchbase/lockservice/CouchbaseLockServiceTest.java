package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.lockservice.DatabaseChangeLogLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static common.constants.TestConstants.TEST_BUCKET;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.CHANGELOG_LOCK_COLLECTION_NAME;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CouchbaseLockServiceTest {

    @Mock
    private Collection collection;
    @Mock
    private CouchbaseConnection connection;
    @Mock
    private ContextServiceProvider serviceProvider;
    @Mock
    private CouchbaseLock couchbaseLock;
    @Mock
    private GetResult getResult;

    private CouchbaseLiquibaseDatabase database;

    private CouchbaseLockService lockService;

    @BeforeEach
    void setUp() {
        database = new CouchbaseLiquibaseDatabase();
        database.setConnection(connection);
        lockService = new CouchbaseLockService();
        lockService.setDatabase(database);
        lockService.setServiceProvider(serviceProvider);
    }

    @Test
    @DisplayName("Test priority of lock service")
    void testPriority() {
        assertThat(lockService.getPriority()).isEqualTo(PRIORITY_SPECIALIZED);
    }

    @Test
    @DisplayName("Test lock service supports CouchbaseLiquibaseDatabase")
    void testSupport() {
        assertTrue(lockService.supports(new CouchbaseLiquibaseDatabase()));
        assertTrue(lockService.supports(database));
    }

    @Test
    void Should_list_empty_locks_array() {
        DatabaseChangeLogLock[] result = lockService.listLocks();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void Should_init_successful_on_first_call_only() {
        when(serviceProvider.getServiceCollection(CHANGELOG_LOCK_COLLECTION_NAME.getCurrentValue())).thenReturn(collection);
        when(collection.bucketName()).thenReturn(TEST_BUCKET);

        assertThatCode(() -> lockService.init()).doesNotThrowAnyException();

        boolean hasLock = lockService.hasChangeLogLock();
        assertThat(hasLock).isFalse();

        verify(serviceProvider).getServiceCollection(any());
        reset(serviceProvider);

        assertThatCode(() -> lockService.init()).doesNotThrowAnyException();

        verify(serviceProvider, never()).getServiceCollection(any());
    }

    @Test
    void Should_release_lock_successfully() {
        // Is required to get FREE status of lock to release it easier.
        when(collection.get(any())).thenThrow(new CouchbaseException("Mocked"));

        acquireLock();

        boolean secondAcquiredLock = lockService.acquireLock();
        assertThat(secondAcquiredLock).isTrue();

        assertThatCode(() -> lockService.releaseLock()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isFalse();
    }

    @Test
    void Should_force_release_lock_successfully() {
        acquireLock();

        assertThatCode(() -> lockService.forceReleaseLock()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isFalse();
        verify(collection).remove(TEST_BUCKET);
    }

    @Test
    void Should_release_lock_on_destroy_successfully() {
        mockLockOwner(lockService.getServiceId());

        acquireLock();

        boolean secondAcquiredLock = lockService.acquireLock();
        assertThat(secondAcquiredLock).isTrue();

        assertThatCode(() -> lockService.destroy()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isFalse();

        verify(collection).remove(TEST_BUCKET);
        verify(couchbaseLock).getOwner();
    }

    @Test
    void Should_try_release_lock_on_destroy_and_catch_exception() {
        mockLockOwner("Owner");

        acquireLock();

        boolean secondAcquiredLock = lockService.acquireLock();
        assertThat(secondAcquiredLock).isTrue();

        assertThatCode(() -> lockService.destroy()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isTrue();

        verify(couchbaseLock).getOwner();
    }

    @Test
    void Should_release_lock_on_reset_successfully() {
        mockLockOwner(lockService.getServiceId());

        acquireLock();

        boolean secondAcquiredLock = lockService.acquireLock();
        assertThat(secondAcquiredLock).isTrue();

        assertThatCode(() -> lockService.reset()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isFalse();

        verify(collection).remove(TEST_BUCKET);
        verify(couchbaseLock).getOwner();
    }

    @Test
    void Should_try_release_lock_on_reset_and_catch_exception() {
        mockLockOwner("Owner");

        acquireLock();

        boolean secondAcquiredLock = lockService.acquireLock();
        assertThat(secondAcquiredLock).isTrue();

        assertThatCode(() -> lockService.reset()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isTrue();

        verify(couchbaseLock).getOwner();
    }

    private void acquireLock() {
        when(serviceProvider.getServiceCollection(CHANGELOG_LOCK_COLLECTION_NAME.getCurrentValue())).thenReturn(collection);
        when(collection.bucketName()).thenReturn(TEST_BUCKET);

        assertThat(lockService.hasChangeLogLock()).isFalse();
        assertThat(lockService.acquireLock()).isTrue();
        assertThat(lockService.hasChangeLogLock()).isTrue();

        verify(collection).insert(any(), any(), any());
    }

    private void mockLockOwner(String owner) {
        when(collection.get(TEST_BUCKET)).thenReturn(getResult);
        when(getResult.contentAs(CouchbaseLock.class)).thenReturn(couchbaseLock);
        when(couchbaseLock.getOwner()).thenReturn(owner);
    }

}
