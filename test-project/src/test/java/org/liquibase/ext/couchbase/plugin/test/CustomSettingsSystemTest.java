package org.liquibase.ext.couchbase.starter.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.plugin.common.ContainerizedTest;
import org.liquibase.ext.couchbase.plugin.containers.JavaMavenContainer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.CHANGELOG_LOCK_COLLECTION_NAME;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.CHANGELOG_RECHECK_TIME;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.CHANGELOG_WAIT_TIME;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.IS_REACTIVE_TRANSACTIONS;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.LOCK_TTL;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.LOCK_TTL_PROLONGATION;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.MUTATE_IN_TIMEOUT;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.REACTIVE_TRANSACTION_PARALLEL_THREADS;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.SERVICE_BUCKET_NAME;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.TRANSACTION_TIMEOUT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.createMavenPluginContainer;

public class CustomSettingsSystemTest extends ContainerizedTest {

    private JavaMavenContainer javaMavenPluginContainer;

    @Test
    @SneakyThrows
    public void Should_set_custom_settings() {
        javaMavenPluginContainer = createMavenPluginContainer(couchbaseContainer, "changelogs/custom-settings-mock-test.xml");

        javaMavenPluginContainer.start();
        while (javaMavenPluginContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertEquals(Duration.ofSeconds(61), CHANGELOG_RECHECK_TIME.getCurrentValue());
        assertEquals(Duration.ofSeconds(46), CHANGELOG_WAIT_TIME.getCurrentValue());
        assertEquals(Duration.ofSeconds(16), LOCK_TTL.getCurrentValue());
        assertEquals(Duration.ofSeconds(11), LOCK_TTL_PROLONGATION.getCurrentValue());
        assertEquals(Duration.ofSeconds(26), TRANSACTION_TIMEOUT.getCurrentValue());
        assertEquals(Duration.ofSeconds(26), MUTATE_IN_TIMEOUT.getCurrentValue());
        assertEquals(9, REACTIVE_TRANSACTION_PARALLEL_THREADS.getCurrentValue());
        assertTrue(IS_REACTIVE_TRANSACTIONS.getCurrentValue());
        assertEquals("customLockCollectionName", CHANGELOG_LOCK_COLLECTION_NAME.getCurrentValue());
        assertEquals("customBucketName", SERVICE_BUCKET_NAME.getCurrentValue());
    }

}
