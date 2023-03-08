package liquibase.ext.couchbase.mapper;

import com.couchbase.client.java.json.JsonObject;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.IncorrectFileException;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
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
    private static final String ID = "id";

    @Override
    public List<Document> map(String filePath) {
        try {
            try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
                return stream.map(JsonObject::fromJson)
                        .map(this::processJson)
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            logger.warning("Incorrect json file provided", e);
            throw new IncorrectFileException(filePath);
        }
    }

    private Document processJson(JsonObject jsonObject) {
        if (!jsonObject.containsKey(ID)) {
            logger.info("Document has no id, can't import, skipping");
            return null;
        }
        return lineToDocument(jsonObject);
    }

    private Document lineToDocument(JsonObject json) {
        return document((String) json.get(ID), json.toString(), DataType.JSON);
    }
}
