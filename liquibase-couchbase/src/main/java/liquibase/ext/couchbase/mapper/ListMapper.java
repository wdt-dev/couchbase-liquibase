package liquibase.ext.couchbase.mapper;

import liquibase.ext.couchbase.types.Document;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

@NoArgsConstructor
public class ListMapper implements DocFileMapper {
    @Override
    public List<Document> map(String filePath) {
        // TODO implement this in scope of the separate task
        return null;
    }
}
