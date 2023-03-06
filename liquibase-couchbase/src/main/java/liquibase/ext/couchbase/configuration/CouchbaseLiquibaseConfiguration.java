package liquibase.ext.couchbase.configuration;

import liquibase.configuration.AutoloadedConfigurations;
import liquibase.configuration.ConfigurationDefinition;

import java.time.Duration;
import java.util.Optional;

/**
 * Configuration class specific for this extension
 */
public class CouchbaseLiquibaseConfiguration implements AutoloadedConfigurations {

    public static final ConfigurationDefinition<String> SERVICE_BUCKET_NAME;
    public static final ConfigurationDefinition<String> CHANGELOG_LOCK_COLLECTION_NAME;
    public static final ConfigurationDefinition<Duration> CHANGELOG_WAIT_TIME;
    public static final ConfigurationDefinition<Duration> CHANGELOG_RECHECK_TIME;
    public static final ConfigurationDefinition<Duration> LOCK_TTL;
    public static final ConfigurationDefinition<Duration> LOCK_TTL_PROLONGATION;

    static {
        ConfigurationDefinition.Builder builder = new ConfigurationDefinition.Builder("liquibase.couchbase");

        CHANGELOG_RECHECK_TIME = builder.define("changelogRecheckTime", Duration.class)
                .addAliasKey("liquibase.couchbase.changelogRecheckTime")
                .setDescription("Change log recheck time")
                .setDefaultValue(Duration.ofSeconds(10L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        CHANGELOG_WAIT_TIME = builder.define("changelogWaitTime", Duration.class)
                .addAliasKey("liquibase.couchbase.changelogWaitTime")
                .setDescription("Time limit to wait for lock in LockService")
                .setDefaultValue(Duration.ofSeconds(300L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        CHANGELOG_LOCK_COLLECTION_NAME = builder.define("changelogCollectionName", String.class)
                .addAliasKey("liquibase.couchbase.changelogWaitTime")
                .setDescription("Collection name in service bucket")
                .setDefaultValue("CHANGELOGLOCKS")
                .build();

        SERVICE_BUCKET_NAME = builder.define("serviceBucketName", String.class)
                .addAliasKey("liquibase.couchbase.serviceBucketName")
                .setDescription("Liquibase service bucket name")
                .setDefaultValue("liquibaseServiceBucket")
                .build();


        LOCK_TTL_PROLONGATION = builder.define("lockservice.ttl-prolongation", Duration.class)
                .addAliasKey("liquibase.couchbase.lockservice.ttl-prolongation")
                .setDescription("Liquibase locks prolongation time")
                .setDefaultValue(Duration.ofMinutes(1L))
                .build();

        LOCK_TTL = builder.define("lockservice.lock-ttl", Duration.class)
                .addAliasKey("liquibase.couchbase.lockservice.lock-ttl")
                .setDescription("Liquibase locks time to live")
                .setDefaultValue(Duration.ofMinutes(3L))
                .build();
    }

    private static Duration durationExtract(Object value) {
        return Optional.ofNullable(value)
                .map(String::valueOf)
                .map(Duration::parse)
                .orElse(null);
    }
}
