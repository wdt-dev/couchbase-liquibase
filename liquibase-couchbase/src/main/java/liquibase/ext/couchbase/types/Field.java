package liquibase.ext.couchbase.types;

import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Document field name, frequently used in indexes
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 * @see liquibase.ext.couchbase.statement.CreatePrimaryQueryIndexStatement
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Field extends AbstractLiquibaseSerializable {

    private String field;

    @Override
    public String getSerializedObjectName() {
        return "field";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }
}
