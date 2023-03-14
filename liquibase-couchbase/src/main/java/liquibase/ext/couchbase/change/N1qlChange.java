package liquibase.ext.couchbase.change;

import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.change.utils.N1qlFileReader;
import liquibase.ext.couchbase.statement.DropBucketStatement;
import liquibase.ext.couchbase.statement.N1qlStatement;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Part of change set package. Responsible for dropping bucket with specified name
 * @see DropBucketStatement
 */
@DatabaseChange(name = "n1ql",
                description = "Executes sql++ couchbase query " + "https://docs.couchbase.com/server/current/getting-started/try-a-query" +
                        ".html",
                priority = PrioritizedService.PRIORITY_DATABASE,
                appliesTo = {"database"})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class N1qlChange extends CouchbaseChange {

    private String filePath;

    @Override
    public String getConfirmationMessage() {
        return String.format("The '%s' query executes successfully", filePath);
    }

    @Override
    public SqlStatement[] generateStatements() {
        String query = N1qlFileReader.load(filePath);
        List<String> queries = Arrays.stream(query.split(";"))
                .map(s -> s.replace("\n", "") + ";")
                .collect(Collectors.toList());
        return new SqlStatement[] {new N1qlStatement(queries)};
    }

}