package liquibase.ext.couchbase.mapper;

import liquibase.ext.couchbase.types.Document;

import java.util.List;

public interface DocFileMapper {
    List<Document> map(String filePath);
}
