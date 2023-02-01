package liquibase.integration;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

public abstract class CouchbaseContainerizedTest {
    protected static final String TEST_BUCKET = "test";
    protected static final String COUCHBASE_VERSION = "7.1.3";
    protected static final DockerImageName IMAGE_NAME = DockerImageName.parse("couchbase/server");

    protected static CouchbaseContainer container;

    @SneakyThrows
    @BeforeAll
    static void setUp() {
        BucketDefinition bucketDefinition = new BucketDefinition(TEST_BUCKET);
        container = new CouchbaseContainer(IMAGE_NAME.withTag(COUCHBASE_VERSION))
                .withBucket(bucketDefinition)
                .withReuse(true)
                .withStartupTimeout(Duration.ofMinutes(2L))
                .waitingFor(Wait.forHealthcheck());
        container.start();
    }

    @AfterAll
    static void tearDown() {
    }
}