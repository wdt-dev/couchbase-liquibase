package org.liquibase.ext.couchbase.cli.test.bucket;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.cli.test.common.ContainerizedTest;
import org.liquibase.ext.couchbase.cli.test.containers.JavaMavenContainer;
import org.liquibase.ext.couchbase.cli.test.containers.LiquibaseContainer;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.liquibase.ext.couchbase.cli.test.common.TestContainerInitializer.createMavenPluginContainer;
import static org.liquibase.ext.couchbase.cli.test.common.TestContainerInitializer.getPathOfShadeJar;

class BucketTest extends ContainerizedTest {
    JavaMavenContainer javaMavenPluginContainer;

    @AfterEach
    void cleanUpEach() {
        javaMavenPluginContainer.stop();
    }

    @Test
    @SneakyThrows
    void Should_create_bucket_scope_collection() {
        javaMavenPluginContainer = createMavenPluginContainer(couchbaseContainer,
                "bucket/changelog.create-bucket.test.xml", getPathOfShadeJar());

        javaMavenPluginContainer.start();
        while (javaMavenPluginContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertTrue(clusterOperator.isBucketExists("createBucketTest"));
    }


//     @Test
//     @SneakyThrows
//     void Should_drop_bucket() {
//         clusterOperator.createBucket("dropBucketTest");
//         liquibaseContainer = createMavenPluginContainer(couchbaseContainer,
//                 "bucket/changelog.drop-bucket.test.xml", getPathOfShadeJar());
//
//         liquibaseContainer.start();
//         while (liquibaseContainer.isRunning()) {
//             TimeUnit.SECONDS.sleep(5L);
//         }
//
//         assertFalse(clusterOperator.isBucketExists("dropBucketTest"));
//     }


}