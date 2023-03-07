package liquibase.ext.couchbase.mapper;

import com.couchbase.client.java.json.JsonObject;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.IncorrectFileException;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import liquibase.logging.Logger;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static liquibase.ext.couchbase.types.Document.document;

@NoArgsConstructor
public class LinesMapper implements DocFileMapper {
    private final Logger logger = Scope.getCurrentScope().getLog(LinesMapper.class);
    private static final String ID = "id";

    @Override
    public List<Document> map(String filePath) {
        List<Document> docs = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                JsonObject jsonObject = JsonObject.fromJson(line);
                if (!jsonObject.containsKey(ID)) {
                    logger.info("Document has no id, can't import, skipping");
                    continue;
                }
                docs.add(lineToDocument(jsonObject));
            }

            reader.close();

            return docs;
        } catch (IOException e) {
            logger.warning("Incorrect json file provided",e);
            throw new IncorrectFileException(filePath);
        }
    }

    private Document lineToDocument(JsonObject json) {
        return document((String) json.get(ID), json.toString(), DataType.JSON);
    }
}
