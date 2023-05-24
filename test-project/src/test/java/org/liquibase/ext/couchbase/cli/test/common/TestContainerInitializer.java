package org.liquibase.ext.couchbase.cli.test.common;

import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import lombok.SneakyThrows;
import org.liquibase.ext.couchbase.cli.test.containers.JavaMavenContainer;
import org.liquibase.ext.couchbase.cli.test.containers.LiquibaseContainer;
import org.liquibase.ext.couchbase.cli.test.util.TestPropertyProvider;
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

import static java.lang.String.format;

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
    private static final String LIQUIBASE_UPDATE_COMMAND_TEMPLATE = "mvn -f /liquibase-couchbase-extension/test-project/pom.xml liquibase:update -DchangeLogFile=changelog-exec.xml -Durl=%s " +
            "-Dusername=%s -Dpassword=%s";
    private static final String JAR_GENERATE_COMMAND = "mvn -f /liquibase-couchbase-extension/liquibase-couchbase/pom.xml clean install -Dmaven.test.skip";
    private static final String MAVEN_CLEAN_PACKAGE_COMMAND = "mvn -f /liquibase-couchbase-extension/test-project/pom.xml clean package -Dmaven.test.skip";
    private static final Integer COUCHBASE_UPDATE_PORT = 11210;

    public static CouchbaseLiquibaseDatabase createDatabase(CouchbaseContainer container) {
        return new CouchbaseLiquibaseDatabase(
                container.getUsername(),
                container.getPassword(),
                container.getConnectionString()
        );
    }

    public static CouchbaseContainer createCocubhaseContainer(String testBucket) {
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

    public static JavaMavenContainer createMavenPluginContainer(CouchbaseContainer couchbaseContainer, String changelog, Path jarFilePath) {
        String connectionString = format("couchbase://%s:%d", COUCHBASE_NETWORK_ALIAS, COUCHBASE_UPDATE_PORT);
        String updateCommand = format(LIQUIBASE_UPDATE_COMMAND_TEMPLATE, connectionString, couchbaseContainer.getUsername(), couchbaseContainer.getPassword());

        MountableFile changelogFile = MountableFile.forClasspathResource(changelog);
        //MountableFile propertiesFile = MountableFile.forClasspathResource(LIQUIBASE_COUCHBASE_PROPERTIES_FILE);
        MountableFile credentialsFile = MountableFile.forClasspathResource(LIQUIBASE_PROPERTIES_FILE);
        //MountableFile jarFile = MountableFile.forHostPath(jarFilePath);

        try (JavaMavenContainer javaMavenPluginContainer = new JavaMavenContainer()
                .withFileSystemBind(getPathOfLiquibaseCouchbaseParentProject().toString(), LIQUIBASE_COUCHBASE_PROJECT_BASE_DIR,
                        BindMode.READ_WRITE)
                .withFileSystemBind(getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project/dependencies", "/root/.m2/repository")
                .withNetwork(couchbaseContainer.getNetwork())
                .withAccessToHost(true)
                .withCopyFileToContainer(changelogFile, "/liquibase-couchbase-extension/test-project/src/test/resources/mvntest/testdata/changelog-exec.xml")
                //.withCopyFileToContainer(propertiesFile, LIQUIBASE_CONTAINER_COUCHBASE_PROPERTIES_PATH)
                .withCopyFileToContainer(credentialsFile, "/liquibase-couchbase-extension/test-project/src/test/resources/mvntest/liquibase.properties")
                .dependsOn(couchbaseContainer)
                .withCommand(MAVEN_CLEAN_PACKAGE_COMMAND)
                .withCommand(updateCommand)) {
            return javaMavenPluginContainer;
        }
    }

    @SneakyThrows
    public static JavaMavenContainer createJavaMavenContainerWithJar() {
        try (JavaMavenContainer javaMavenContainer = new JavaMavenContainer()) {
            javaMavenContainer.withFileSystemBind(getPathOfLiquibaseCouchbaseParentProject().toString(), LIQUIBASE_COUCHBASE_PROJECT_BASE_DIR,
                            BindMode.READ_WRITE)
                    .withFileSystemBind(getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project/dependencies", "/root/.m2/repository")
                    .withCommand(JAR_GENERATE_COMMAND)
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

    public static Path getPathOfShadeJar() {
        Path basePathObj = Paths.get(TEST_PROJECT_ABSOLUTE_PATH);
        return basePathObj.resolveSibling(LIQUIBASE_COUCHBASE_PROJECT_RELATIVE_PATH);
    }

}
