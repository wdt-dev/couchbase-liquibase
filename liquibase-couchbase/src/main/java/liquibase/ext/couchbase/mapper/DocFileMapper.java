package liquibase.ext.couchbase.mapper;

import liquibase.Scope;
import liquibase.ext.couchbase.provider.factory.DocumentKeyProviderFactory;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;

import java.util.List;

public interface DocFileMapper {
    DocumentKeyProviderFactory keyProviderFactory = Scope.getCurrentScope()
            .getSingleton(DocumentKeyProviderFactory.class);
    List<Document> map(File file);
}
