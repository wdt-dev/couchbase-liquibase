package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Collection;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
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

/*    private void reset() {
        LockServiceFactory.reset();
        Scope.getCurrentScope().getSingleton(ExecutorService.class).setExecutor(EXECUTOR_NAME, database, executor);
        Scope.getCurrentScope().getSingleton(ExecutorService.class).reset();
    }*/

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
        when(serviceProvider.getServiceCollection(CHANGELOG_LOCK_COLLECTION_NAME.getCurrentValue())).thenReturn(collection);
        when(collection.bucketName()).thenReturn(TEST_BUCKET);
        when(collection.get(any())).thenThrow(new CouchbaseException("Mocked"));

        assertThat(lockService.hasChangeLogLock()).isFalse();

        boolean acquiredLock = lockService.acquireLock();
        assertThat(acquiredLock).isTrue();

        assertThat(lockService.hasChangeLogLock()).isTrue();

        verify(collection).insert(any(), any(), any());

        boolean secondAcquiredLock = lockService.acquireLock();
        assertThat(secondAcquiredLock).isEqualTo(acquiredLock);

        assertThatCode(() -> lockService.releaseLock()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isFalse();
    }


}
