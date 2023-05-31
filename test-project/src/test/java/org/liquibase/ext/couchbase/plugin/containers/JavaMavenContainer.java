package org.liquibase.ext.couchbase.plugin.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;

import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.ROOT_WITH_TEST_PROJECT_TEST_RESOURCES;

public class JavaMavenContainer extends GenericContainer<JavaMavenContainer> {

    public JavaMavenContainer() {
        super(new ImageFromDockerfile().withFileFromPath("Dockerfile",
                Paths.get(ROOT_WITH_TEST_PROJECT_TEST_RESOURCES + "Dockerfile")));
    }
}
