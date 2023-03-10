package liquibase.ext.couchbase.change;

import com.couchbase.client.java.kv.MutateInOptions;
import com.couchbase.client.java.kv.MutateInSpec;
import com.couchbase.client.java.kv.StoreSemantics;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.MutateInStatement;
import liquibase.ext.couchbase.transformer.MutateInSpecTransformer;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.couchbase.client.java.kv.MutateInOptions.mutateInOptions;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.MUTATE_IN_TIMEOUT;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;

@Data
@DatabaseChange(
        name = "mutateIn",
        description = "https://docs.couchbase.com/java-sdk/current/howtos/subdocument-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
@AllArgsConstructor
public class MutateInChange extends CouchbaseChange {

    private String id;
    private String bucketName;
    private String scopeName;
    private String collectionName;
    private String expiry;
    private String preserveExpiry;
    private String storeSemantics;

    private List<LiquibaseMutateInSpec> mutateInSpecs = new ArrayList<>();

    private static final MutateInSpecTransformer mutateInSpecTransformer = new MutateInSpecTransformer();

    @Override
    public SqlStatement[] generateStatements() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        MutateIn mutate = buildMutate(keyspace);
        MutateInOptions mutateInOptions = buildOptions(expiry, preserveExpiry, storeSemantics);
        return new SqlStatement[] {
                new MutateInStatement(mutate, mutateInOptions)
        };
    }

    private MutateInOptions buildOptions(String expiry, String preserveExpiry, String storeSemantics) {
        MutateInOptions options = mutateInOptions();
        options.timeout(MUTATE_IN_TIMEOUT.getCurrentValue());
        optionalWithNullFilter(expiry)
                .ifPresent(value -> options.expiry(Duration.parse(value)));
        optionalWithNullFilter(preserveExpiry)
                .ifPresent(value -> options.preserveExpiry(Boolean.parseBoolean(value)));
        optionalWithNullFilter(storeSemantics)
                .ifPresent(value -> options.storeSemantics(StoreSemantics.valueOf(storeSemantics)));
        return options;
    }

    private Optional<String> optionalWithNullFilter(String storeSemantics) {
        return Optional.ofNullable(storeSemantics).filter(StringUtils::isNotEmpty);
    }

    @Override
    public String getConfirmationMessage() {
        int opCount = mutateInSpecs.size();
        return format("MutateIn %s operations has been successfully fulfilled", opCount);
    }

    private MutateIn buildMutate(Keyspace keyspace) {
        List<MutateInSpec> specs = mutateInSpecs.stream()
                .map(mutateInSpecTransformer::toSpec)
                .collect(toList());
        return MutateIn.builder()
                .id(id)
                .keyspace(keyspace)
                .specs(specs)
                .build();
    }

}
