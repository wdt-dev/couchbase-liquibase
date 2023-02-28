package liquibase.ext.couchbase.provider;


import lombok.NonNull;

import java.util.Properties;

public interface PropertyProvider {


    /**
     * Lookup firstly in Env properties and then in property file
     * @param name - Property name
     * @return not null value or
     *
     * @throws IllegalArgumentException if property isn't provided
     */
    @NonNull
    static String getProperty(String name, Properties properties) {
        String property = findPropertyValue(name, properties);
        if (property == null) {
            throw new IllegalArgumentException("No such registered property: " + name);
        }

        return property;
    }

    /**
     * Lookup firstly in Env properties and then in property file
     * @param name - Property name
     * @return not null value or default value
     */
    @NonNull
    static String getPropertyOrDefault(String name, String defaultValue, Properties properties) {
        String property = findPropertyValue(name, properties);
        if (property == null) {
            return defaultValue;
        }

        return property;
    }

    @NonNull
    static String findPropertyValue(String name, Properties properties) {
        String environmentValue = System.getenv(name);
        if (environmentValue != null) {
            return environmentValue;
        }

        return properties.getProperty(name);
    }

}
