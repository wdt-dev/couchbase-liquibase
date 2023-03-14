package system.change;

import liquibase.Liquibase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_SQL_TEST;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.matchers.CouchBaseBucketAssert.assertThat;

public class N1qlSystemTest extends LiquibaseSystemTest {

    @Test
    @SneakyThrows
    void Collection_should_be_created_after_liquibase_execution_n1ql() {
        Liquibase liquibase = liquibase(CREATE_COLLECTION_SQL_TEST);

        liquibase.update();

        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope("testCol1", "testScope");
        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope("testCol2", "testScope");
    }
}
