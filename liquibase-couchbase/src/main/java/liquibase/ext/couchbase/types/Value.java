package liquibase.ext.couchbase.types;

import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.exception.MutateInTypeUnsupportedException;
import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Document value(s). Used in MutateIn
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Value extends AbstractLiquibaseSerializable {

    private String data;
    private DataType type;

    @Override
    public String getSerializedObjectName() {
        return "value";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }

    public void setType(String type) {
        this.type = DataType.getByName(type);
    }

    public Object mapDataToType() {
        switch (type) {
            case LONG:
                return Long.valueOf(data);
            case DOUBLE:
                return Double.valueOf(data);
            case BOOLEAN:
                return Boolean.valueOf(data);
            case STRING:
                return data;
            case JSON:
                return JsonObject.fromJson(data);
            case JSON_ARRAY:
                return JsonArray.fromJson(data);
            default:
                throw new MutateInTypeUnsupportedException(type);
        }
    }

}
