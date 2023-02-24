package integration.statement;

import com.couchbase.client.core.error.ScopeExistsException;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.CreateScopeStatement;
import liquibase.ext.couchbase.statement.ScopeExistsStatement;
import liquibase.ext.couchbase.types.Keyspace;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class CreateScopeStatementTest extends RandomizedScopeTestCase {


    private Keyspace keyspace;

    @BeforeEach
    public void setUp() {
        keyspace = keyspace(bucketName, scopeName, collectionName);
    }

    @Test
    void Should_create_scope() {

        String scopeName = "testScopeName";
        //scope don't exists
        ScopeExistsStatement statement = new ScopeExistsStatement(bucketName, scopeName);
        Assertions.assertThat(statement.isTrue(database.getConnection())).isFalse();
        //create scope
        CreateScopeStatement createScopeStatement = new CreateScopeStatement(scopeName, keyspace);
        createScopeStatement.execute(database.getConnection());
//         scope exists
        Assertions.assertThat(statement.isTrue(database.getConnection())).isTrue();
    }


    @Test
    void Should_throw_exception_if_scope_exists() {
        CreateScopeStatement statement = new CreateScopeStatement("scopeNameToTryCreateTwice", keyspace);
        statement.execute(database.getConnection());

        assertThatExceptionOfType(ScopeExistsException.class)
            .isThrownBy(() -> statement.execute(database.getConnection()));
    }

}