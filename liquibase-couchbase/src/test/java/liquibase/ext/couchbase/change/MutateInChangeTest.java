package liquibase.ext.couchbase.change;

import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateInType;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_INSERT_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

public class MutateInChangeTest {

    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
        ChangeLogProvider provider = new TestChangeLogProvider(database);
        changeLog = provider.load(MUTATE_IN_INSERT_TEST_XML);
    }

    @Test
    void Should_parse_mutateIn_correctly() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());

        assertThat(changeSet.getChanges())
                .map(MutateInChange.class::cast)
                .containsExactly(
                        change(asList(spec("user.age", "29", DataType.STRING, MutateInType.INSERT)))
                );
    }

    @Test
    void Should_has_correct_confirm_msg() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        Change change = firstOf(changeSet.getChanges());

        assertThat(change.getConfirmationMessage())
                .isEqualTo("MutateIn %s operations has been successfully fulfilled", 1);
    }

    private LiquibaseMutateInSpec spec(String path, String value, DataType dataType, MutateInType type) {
        return new LiquibaseMutateInSpec(path, new Value(value, dataType), type);
    }

    private MutateInChange change(List<LiquibaseMutateInSpec> specs) {
        return new MutateInChange(
                TEST_ID,
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION,
                specs
        );
    }
}