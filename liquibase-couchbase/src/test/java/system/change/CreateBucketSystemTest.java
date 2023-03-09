package system.change;

import common.matchers.CouchBaseClusterAssert;
import liquibase.Liquibase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquiBaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.CREATE_BUCKET_TEST_JSON;
import static common.constants.ChangeLogSampleFilePaths.CREATE_BUCKET_TEST_XML;
import static common.constants.TestConstants.CREATE_BUCKET_TEST_NAME;
import static common.constants.TestConstants.CREATE_BUCKET_TEST_NAME_1;

public class CreateBucketSystemTest extends LiquiBaseSystemTest {

    @Test
    @SneakyThrows
    void Bucket_should_be_created() {
        Liquibase liquibase = liquiBase(CREATE_BUCKET_TEST_XML);
        liquibase.update();
        CouchBaseClusterAssert.assertThat(cluster).hasBucket(CREATE_BUCKET_TEST_NAME);
    }

    @Test
    @SneakyThrows
    void Bucket_should_be_created_json() {
        Liquibase liquibase = liquiBase(CREATE_BUCKET_TEST_JSON);
        liquibase.update();
        CouchBaseClusterAssert.assertThat(cluster).hasBucket(CREATE_BUCKET_TEST_NAME_1);
    }
}
