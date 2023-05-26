package org.liquibase.ext.couchbase.plugin.containers;

import org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;

public class JavaMavenContainer extends GenericContainer<JavaMavenContainer> {

    public JavaMavenContainer() {
        // super(new ImageFromDockerfile().withFileFromClasspath("Dockerfile", "Dockerfile"));
        super(new ImageFromDockerfile().withFileFromPath("Dockerfile", Paths.get(
                TestContainerInitializer.getPathOfLiquibaseCouchbaseParentProject().toString() + "/test-project/src/test" +
                        "/resources/Dockerfile")));
    }
}
