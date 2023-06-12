package liquibase.ext.couchbase.types;

import liquibase.serializer.LiquibaseSerializable;
import org.junit.jupiter.api.Test;

import static liquibase.serializer.LiquibaseSerializable.STANDARD_CHANGELOG_NAMESPACE;
import static org.assertj.core.api.Assertions.assertThat;

public class FieldTest {

    private final Field field = new Field();

    @Test
    void Should_return_expected_serialized_object_name() {
        assertThat(field.getSerializedObjectName()).isEqualTo("field");
    }

    @Test
    void Should_return_expected_serialized_object_namespace() {
        assertThat(field.getSerializedObjectNamespace()).isEqualTo(STANDARD_CHANGELOG_NAMESPACE);
    }

    @Test
    void Should_return_expected_serialized_field_type() {
        assertThat(field.getSerializableFieldType(null)).isEqualTo(LiquibaseSerializable.SerializationType.DIRECT_VALUE);
    }
}
