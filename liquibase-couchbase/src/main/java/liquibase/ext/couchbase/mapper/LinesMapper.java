package liquibase.ext.couchbase.mapper;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static liquibase.ext.couchbase.types.Document.document;

@NoArgsConstructor
public class LinesMapper implements DocFileMapper {
    @Override
    public List<Document> map(String filePath) {
        List<Document> docs = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = reader.readLine()) != null) {
                JsonObject jsonObject = JsonObject.fromJson(line);
                docs.add(lineToDocument(jsonObject));
            }

            reader.close();

            return docs;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document lineToDocument(JsonObject json) {
        return document((String) json.get("key"), json.get("value").toString(), DataType.JSON);
    }
}
