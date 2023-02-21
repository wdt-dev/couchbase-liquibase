package common.matchers;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.ReactiveScope;
import com.couchbase.client.java.json.JsonObject;
import liquibase.changelog.ChangeSet;
import liquibase.ext.couchbase.changelog.CouchbaseChangeLog;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;
import reactor.core.publisher.Flux;

import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;


public class ChangeLogAssert extends AbstractAssert<ChangeLogAssert, ReactiveScope> {

    private String key;
    private ReactiveScope scope;
    private ReactiveCollection collection;

    private CouchbaseChangeLog changeLog;

    private ChangeLogAssert(ReactiveScope scope) {
        super(scope, ChangeLogAssert.class);
        this.scope = scope;
        this.collection = scope.collection(CHANGE_LOG_COLLECTION);
    }


    public static ChangeLogAssert assertThat(@NonNull ReactiveScope scope) {
        return new ChangeLogAssert(scope);
    }

    public ChangeLogAssert documentsSizeEqualTo(@NonNull int numberOfDocuments) {
        Flux<JsonObject> documents = scope.query("Select * from DATABASECHANGELOG").block().rowsAsObject();
        if (documents.count().block() != numberOfDocuments) {
            failWithMessage("Size of documents not equals to <%s>, actual number is <%s>", numberOfDocuments,
                    documents.count().block());
        }

        return this;
    }

    public ChangeLogAssert hasDocument(@NonNull String key) {
        try {
            changeLog = collection.get(key).block().contentAs(CouchbaseChangeLog.class);
            this.key = key;
        }
        catch (DocumentNotFoundException ex) {
            failWithMessage("ChangeLog with key <%s> not exists", key);
        }

        return this;
    }

    public ChangeLogAssert hasNoDocument(@NonNull String key) {
        try {
            changeLog = collection.get(key).block().contentAs(CouchbaseChangeLog.class);
            failWithMessage("ChangeLog with key <%s> exists", key);
        }
        catch (DocumentNotFoundException ignored) {
        }

        return this;
    }

    public ChangeLogAssert withExecType(@NonNull ChangeSet.ExecType execType) {
        if (!changeLog.getExecType().equals(execType)) {
            failWithMessage("ChangeLog with key <%s> doesn't contain execType <%s>, actual type is <%s>", key, execType,
                    changeLog.getExecType());
        }

        return this;
    }

    public ChangeLogAssert withOrder(@NonNull int order) {
        if (changeLog.getOrderExecuted() != order) {
            failWithMessage("ChangeLog with key <%s> doesn't have order <%s>, actual order is <%s>", key, order,
                    changeLog.getOrderExecuted());
        }

        return this;
    }

}
