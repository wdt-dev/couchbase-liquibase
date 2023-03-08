package liquibase.ext.couchbase.types;

import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * File to import
 * @see AbstractLiquibaseSerializable
 * @see liquibase.serializer.LiquibaseSerializable
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class File extends AbstractLiquibaseSerializable {

    private String filePath;
    private ImportType importType;
    private KeyProviderType keyProviderType;
    private String keyProviderExpression;

    @Override
    public String getSerializedObjectName() {
        return "file";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }

    @Override
    public SerializationType getSerializableFieldType(String field) {
        return SerializationType.DIRECT_VALUE;
    }

    public void setImportType(String importType) {
        this.importType = ImportType.getByName(importType);
    }

    public void setKeyProviderType(String keyProviderType) {
        this.keyProviderType = KeyProviderType.valueOf(keyProviderType);
    }

    public void setKeyProviderExpression(String keyProviderExpression) {
        this.keyProviderExpression = keyProviderExpression;
    }
}
