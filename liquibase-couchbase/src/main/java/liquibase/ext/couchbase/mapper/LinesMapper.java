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

/**
 * Document mapper for LINES mode (equals to cbimport LINES mode), when every line in file consider as document
 *
 * @link <a href="https://docs.couchbase.com/server/current/tools/cbimport-json.html#list">cbimport documentation</a>
 */
@NoArgsConstructor
public class LinesMapper implements DocFileMapper {
    private final Logger logger = Scope.getCurrentScope().getLog(LinesMapper.class);

    @Override
    public List<Document> map(File file) {
        try (Stream<String> stream = Files.lines(Paths.get(file.getFilePath()))) {
            return getDocumentsFromFile(file, stream);
        } catch (IOException e) {
            logger.warning("Incorrect json file provided", e);
            throw new IncorrectFileException(file.getFilePath());
        }
    }

    private List<Document> getDocumentsFromFile(File file, Stream<String> linesFromFile) {
        DocumentKeyProvider<String, JsonObject> keyProvider =
                (DocumentKeyProvider<String, JsonObject>) keyProviderFactory.getKeyProvider(file);

        return linesFromFile
                .map(JsonObject::fromJson)
                .map(json -> document(keyProvider.getKey(json), json))
                .collect(Collectors.toList());
    }
}
