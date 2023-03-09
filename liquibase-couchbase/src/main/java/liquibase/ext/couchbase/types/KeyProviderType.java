package liquibase.ext.couchbase.types;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Getter
public enum KeyProviderType {
    DEFAULT("default"),
    UID("uid"),
    RANDOM("random");

    private final String name;
    private static final Map<String, KeyProviderType> nameValueMap;

    static {
        nameValueMap = Arrays.stream(values())
                .collect(toMap(KeyProviderType::getName, Function.identity()));
    }

    KeyProviderType(String name) {
        this.name = name;
    }

    public static KeyProviderType getByName(String name) {
        return nameValueMap.get(name);
    }
}
