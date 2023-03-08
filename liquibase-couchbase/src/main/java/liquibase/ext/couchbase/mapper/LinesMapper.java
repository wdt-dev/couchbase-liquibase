package liquibase.ext.couchbase.mapper;

import com.couchbase.client.java.json.JsonObject;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.IncorrectFileException;
import liquibase.ext.couchbase.provider.DocumentKeyProvider;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.File;
import liquibase.logging.Logger;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static liquibase.ext.couchbase.types.Document.document;

@NoArgsConstructor
public class LinesMapper implements DocFileMapper {
    private final Logger logger = Scope.getCurrentScope().getLog(LinesMapper.class);

    @Override
    public List<Document> map(File file) {
        try {
            DocumentKeyProvider<String, JsonObject> keyProvider =
                    (DocumentKeyProvider<String, JsonObject>) keyProviderFactory.getKeyProvider(file);
            try (Stream<String> stream = Files.lines(Paths.get(file.getFilePath()))) {
                return stream.map(JsonObject::fromJson)
                        .map(json -> lineToDocument(keyProvider.getKey(json), json))
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            logger.warning("Incorrect json file provided", e);
            throw new IncorrectFileException(file.getFilePath());
        }
    }

    private Document lineToDocument(String key, JsonObject json) {
        return document(key, json);
    }
}
