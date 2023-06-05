package liquibase.ext.couchbase.provider.generator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UidKeyGeneratorTest {

    @Test
    public void Should_generate_uuid() {
        UidKeyGenerator uidKeyGenerator = new UidKeyGenerator();
        String uuid = uidKeyGenerator.generate();
        assertThat(uuid).isNotNull();
        assertThat(uuid.length()).isEqualTo(36);
    }
}
