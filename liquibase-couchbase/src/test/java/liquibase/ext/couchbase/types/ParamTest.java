package liquibase.ext.couchbase.types;

import liquibase.serializer.LiquibaseSerializable;
import org.junit.jupiter.api.Test;

import static liquibase.serializer.LiquibaseSerializable.STANDARD_CHANGELOG_NAMESPACE;
import static org.assertj.core.api.Assertions.assertThat;

class ParamTest {

    private final Param param = new Param();

    @Test
    void Should_return_expected_serialized_object_name() {
        assertThat(param.getSerializedObjectName()).isEqualTo("param");
    }

    @Test
    void Should_return_expected_serialized_object_namespace() {
        assertThat(param.getSerializedObjectNamespace()).isEqualTo(STANDARD_CHANGELOG_NAMESPACE);
    }

    @Test
    void Should_return_expected_serialized_field_type() {
        assertThat(param.getSerializableFieldType(null)).isEqualTo(LiquibaseSerializable.SerializationType.DIRECT_VALUE);
    }
}
