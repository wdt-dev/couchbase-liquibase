package liquibase.ext.couchbase.types;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * Types of import from file (based on cbimport tool formats)
 * @link <a href="https://docs.couchbase.com/server/current/tools/cbimport-json.html"/>
 */
@Getter
public enum ImportType {
    LINES("LINES"),
    LIST("LIST"),
    SAMPLE("SAMPLE"),
    KEY_GENERATORS("KEY_GENERATORS");

    private final String name;

    private static final Map<String, ImportType> allValues;

    static {
        allValues = Arrays.stream(values())
            .collect(toMap(ImportType::getName, Function.identity()));
    }

    ImportType(String name) {
        this.name = name;
    }

    public static ImportType getByName(String name) {
        return allValues.get(name);
    }
}
