package liquibase.ext.couchbase.mapper;

import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class ListMapper implements DocFileMapper {
    @Override
    public List<Document> map(File file) {
        // TODO implement this in scope of the separate task
        return null;
    }
}
