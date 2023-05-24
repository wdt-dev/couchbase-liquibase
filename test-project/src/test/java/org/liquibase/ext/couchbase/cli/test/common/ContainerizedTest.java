package org.liquibase.ext.couchbase.cli.test.common;

import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.operator.ClusterOperator;
import org.testcontainers.couchbase.CouchbaseContainer;

import static org.liquibase.ext.couchbase.cli.test.common.TestContainerInitializer.createCocubhaseContainer;
import static org.liquibase.ext.couchbase.cli.test.common.TestContainerInitializer.createDatabase;

public abstract class ContainerizedTest {

    protected static CouchbaseContainer couchbaseContainer;
    protected static Cluster cluster;
    protected static ClusterOperator clusterOperator;

    protected static final String TEST_BUCKET = "testBucket";
    protected static final String TEST_SCOPE = "testScope";
    protected static final String TEST_COLLECTION = "testCollection";

    static {
        couchbaseContainer = createCocubhaseContainer(TEST_BUCKET);
        couchbaseContainer.start();
        CouchbaseLiquibaseDatabase database = createDatabase(couchbaseContainer);
        cluster = database.getConnection().getCluster();
        clusterOperator = new ClusterOperator(cluster);

        TestContainerInitializer.createJavaMavenContainerWithJar();
    }
}
