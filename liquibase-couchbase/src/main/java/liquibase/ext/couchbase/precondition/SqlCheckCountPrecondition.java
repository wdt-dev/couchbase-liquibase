package liquibase.ext.couchbase.precondition;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.SqlCheckCountPreconditionException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A precondition that checks sql query returns expected count. The sql++ query need to be written by select ... as count template in order
 * to be able to extract count from json. Otherwise result may be not as expected
 * @see AbstractCouchbasePrecondition
 * @see liquibase.precondition.AbstractPrecondition
 * @see SqlCheckCountPreconditionException
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlCheckCountPrecondition extends AbstractCouchbasePrecondition {

    private Integer count;

    private String query;


    @Override
    public String getName() {
        return "sqlCheckCount";
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws SqlCheckCountPreconditionException {
        if (!doesQueryHaveExpectedResult((CouchbaseConnection) database.getConnection())) {
            throw new SqlCheckCountPreconditionException(query, count, changeLog, this);
        }
    }

    public boolean doesQueryHaveExpectedResult(CouchbaseConnection connection) {
        ClusterOperator operator = new ClusterOperator(connection.getCluster());
        QueryResult result = operator.executeSingleSql(query);
        List<JsonObject> rows = result.rowsAsObject();
        return rows.get(0).getInt("count").equals(count);
    }
}
