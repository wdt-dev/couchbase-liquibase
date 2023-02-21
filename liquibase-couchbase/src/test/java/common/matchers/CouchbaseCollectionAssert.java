package common.matchers;

import com.couchbase.client.java.ReactiveCollection;
import liquibase.ext.couchbase.types.Document;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class CouchbaseCollectionAssert extends AbstractAssert<CouchbaseCollectionAssert, ReactiveCollection> {

    private CouchbaseCollectionAssert(ReactiveCollection collection) {
        super(collection, CouchbaseCollectionAssert.class);
    }

    public static CouchbaseCollectionAssert assertThat(@NonNull ReactiveCollection actual) {
        return new CouchbaseCollectionAssert(actual);
    }

    public CouchbaseCollectionAssert hasDocument(@NonNull String id) {
        if (!requireNonNull(actual.exists(id).block()).exists()) {
            failWithMessage("Collection [<%s>] doesn't contains document with ID [<%s>] in scope [<%s>]", actual.name(),
                    id, actual.scopeName());
        }

        return this;
    }

    public CouchbaseCollectionAssert hasNoDocument(@NonNull String id) {
        if (requireNonNull(actual.exists(id).block()).exists()) {
            failWithMessage("Collection [<%s>] contains document with ID [<%s>] in scope [<%s>]", actual.name(), id,
                    actual.scopeName());
        }

        return this;
    }

    public CouchbaseCollectionAssert hasDocuments(@NonNull String... ids) {
        for (String id : ids) {
            hasDocument(id);
        }
        return this;
    }

    public CouchbaseCollectionAssert hasNoDocuments(@NonNull String... ids) {
        for (String id : ids) {
            hasNoDocument(id);
        }
        return this;
    }

    public CouchBaseDocumentAssert extractingDocument(@NonNull String id) {
        hasDocument(id);
        return new CouchBaseDocumentAssert(requireNonNull(actual.get(id).block()).contentAsObject());
    }


    public CouchbaseCollectionAssert containDocuments(List<Document> testDocuments) {
        testDocuments.forEach((doc) -> extractingDocument(doc.getId()).itsContentEquals(doc.getContent()));
        return this;
    }

}
