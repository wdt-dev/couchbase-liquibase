package liquibase.ext.couchbase.types;

import lombok.Getter;

@Getter
public enum KeyProviderType {
    DEFAULT("default"),
    UID("uid"),
    RANDOM("random");

    private final String name;

    KeyProviderType(String name) {
        this.name = name;
    }
}
