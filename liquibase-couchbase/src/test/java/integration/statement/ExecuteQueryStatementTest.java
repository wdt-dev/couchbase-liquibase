package integration.statement;

import com.couchbase.client.core.error.ParsingFailureException;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.ExecuteQueryStatement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


class ExecuteQueryStatementTest extends RandomizedScopeTestCase {

    @Test
    void Should_execute_query() {
        ExecuteQueryStatement executeQueryStatement = new ExecuteQueryStatement("SELECT * FROM system:datastores");
        assertDoesNotThrow(() -> executeQueryStatement.execute(clusterOperator));
    }

    @Test
    void Should_throw_exception() {
        ExecuteQueryStatement executeQueryStatement = new ExecuteQueryStatement("Wrong query");
        assertThatExceptionOfType(ParsingFailureException.class)
                .isThrownBy(() -> executeQueryStatement.execute(clusterOperator));
    }

}