package org.liquibase.ext.couchbase.plugin.common;

import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import lombok.SneakyThrows;
import org.liquibase.ext.couchbase.plugin.containers.JavaMavenContainer;
import org.liquibase.ext.couchbase.plugin.util.TestPropertyProvider;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class TestContainerInitializer {
    private static final String COUCHBASE_IMAGE_NAME = TestPropertyProvider.getProperty("couchbase.image.name");
    private static final String COUCHBASE_IMAGE_VERSION = TestPropertyProvider.getProperty("couchbase.version");
    private static final String TEST_PROJECT_ABSOLUTE_PATH = System.getProperty("user.dir");
    private static final String COUCHBASE_NETWORK_ALIAS = "couchbase";
    private static final String CONTAINER_JAR_PATH = "/liquibase/internal/lib/liquibase_couchbase.jar";
    private static final String LIQUIBASE_CONTAINER_CHANGELOG_PATH = "/liquibase/changelog-exec.xml";
    private static final String LIQUIBASE_COUCHBASE_PROJECT_BASE_DIR = "/liquibase-couchbase-extension";
    private static final String LIQUIBASE_CONTAINER_COUCHBASE_PROPERTIES_PATH = "/liquibase/liquibase-couchbase.properties";
    private static final String LIQUIBASE_CONTAINER_PROPERTIES_PATH = "/liquibase/liquibase.properties";
    private static final String LIQUIBASE_COUCHBASE_PROJECT_RELATIVE_PATH = "liquibase-couchbase/target/liquibase-couchbase-0.1" +
            ".0-SNAPSHOT.jar";
    private static final String LIQUIBASE_COUCHBASE_PROPERTIES_FILE = "liquibase-couchbase.properties";
    private static final String LIQUIBASE_PROPERTIES_FILE = "liquibase.properties";
    private static final String LIQUIBASE_UPDATE_COMMAND_TEMPLATE = "mvn -f /test-project/pom.xml " +
            "liquibase:update -DchangeLogFile=changelog-exec.xml";
    private static final String MAVEN_CLEAN_PACKAGE_COMMAND = "mvn -f /liquibase-couchbase-extension/test-project/pom.xml clean package " +
            "-Dmaven.test.skip";
    private static final Integer COUCHBASE_UPDATE_PORT = 11210;

    public static CouchbaseLiquibaseDatabase createDatabase(CouchbaseContainer container) {
        return new CouchbaseLiquibaseDatabase(
                container.getUsername(),
                container.getPassword(),
                container.getConnectionString()
        );
    }

    public static CouchbaseContainer createCouchbaseContainer(String testBucket) {
        Network network = Network.newNetwork();
        BucketDefinition bucketDef = new BucketDefinition(testBucket).withPrimaryIndex(false);

        try (CouchbaseContainer couchbaseContainer = new CouchbaseContainer(
                DockerImageName.parse(COUCHBASE_IMAGE_NAME).withTag(COUCHBASE_IMAGE_VERSION))
                .withBucket(bucketDef)
                .withServiceQuota(CouchbaseService.KV, 512)
                .withStartupTimeout(Duration.ofMinutes(2L))
                .withNetwork(network)
                .withNetworkAliases(COUCHBASE_NETWORK_ALIAS)
                .waitingFor(Wait.forHealthcheck())) {
            return couchbaseContainer;
        }
    }

    public static JavaMavenContainer createMavenPluginContainer(CouchbaseContainer couchbaseContainer, String changelog) {
        MountableFile changelogFile = MountableFile.forHostPath(
                getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project/src/test/resources/" + changelog);
        MountableFile liquibaseCouchbaseFile = MountableFile.forHostPath(
                getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project/src/test/resources/mvntest/liquibase-couchbase" +
                        ".properties");
        MountableFile credentialsFile = MountableFile.forHostPath(
                getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project/src/test/resources/" + LIQUIBASE_PROPERTIES_FILE);
        MountableFile liquibaseUpdateShFile = MountableFile.forHostPath(
                getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project/src/test/resources/liquibase-update-command.sh");

        try (JavaMavenContainer javaMavenPluginContainer = new JavaMavenContainer()
                .withFileSystemBind(getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project/dependencies",
                        "/root/.m2/repository")
                .withNetwork(couchbaseContainer.getNetwork())
                .withAccessToHost(true)
                .withCopyFileToContainer(MountableFile.forHostPath(
                        getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project"), "/test-project")
                .withCopyFileToContainer(changelogFile,
                        "/test-project/src/main/resources/liquibase/changelog-root.xml")
                .withCopyFileToContainer(liquibaseCouchbaseFile,
                        "/test-project/src/main/resources/liquibase-couchbase.properties")
                .withCopyFileToContainer(credentialsFile,
                        "/test-project/src/main/resources/liquibase/liquibase.properties")
                .withCopyFileToContainer(liquibaseUpdateShFile, "liquibase-update-command.sh")
                .dependsOn(couchbaseContainer)
                .withCommand("sh liquibase-update-command.sh")) {
            return javaMavenPluginContainer;
        }
    }

    @SneakyThrows
    public static JavaMavenContainer createJavaMavenContainerWithJar() {
        try (JavaMavenContainer javaMavenContainer = new JavaMavenContainer()) {
            javaMavenContainer
                    .withFileSystemBind(getPathOfLiquibaseCouchbaseParentProject().toString(),
                            LIQUIBASE_COUCHBASE_PROJECT_BASE_DIR,
                            BindMode.READ_WRITE)
                    .withFileSystemBind(getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project/dependencies",
                            "/root/.m2/repository")
                    .withCopyFileToContainer(MountableFile.forClasspathResource("build-dependency.sh"),
                            "build-dependency.sh")
                    .withCommand("sh build-dependency.sh")
                    .start();
            while (javaMavenContainer.isRunning()) {
                TimeUnit.SECONDS.sleep(5L);
            }
            return javaMavenContainer;
        }
    }

    public static Path getPathOfLiquibaseCouchbaseParentProject() {
        return Paths.get(TEST_PROJECT_ABSOLUTE_PATH).getParent();
    }

}
