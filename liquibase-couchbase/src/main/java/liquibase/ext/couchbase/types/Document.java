package liquibase.ext.couchbase.types;

import com.couchbase.client.java.json.JsonObject;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a Couchbase document, contain it's key and JSON value
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document extends AbstractLiquibaseSerializable {

    private String id;
    private Value value = new Value();

    @Override
    public String getSerializedObjectName() {
        return "document";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    public static Document document(String id, String content, DataType type) {
        return new Document(id, new Value(content, type));
    }

    public Document data(String data) {
        this.value.setData(data);
        return this;
    }

    public Document id(String id) {
        this.setId(id);
        return this;
    }

    public Document type(String type) {
        this.value.setType(type);
        return this;
    }

    public Document value(Value value) {
        this.setValue(value);
        return this;
    }

    public static Document document(String id, String content, String type) {
        return new Document().id(id).data(content).type(type);
    }

    public static Document document(String id, Value value) {
        return new Document().id(id).value(value);
    }

    public Object getContentAsObject() {
        return value.mapDataToType();
    }

    public JsonObject getContentAsJson() {
        return (JsonObject) getContentAsObject();
    }

    public List<Field> getFields() {
        return getContentAsJson().getNames().stream().map(Field::new).collect(Collectors.toList());
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }

}
