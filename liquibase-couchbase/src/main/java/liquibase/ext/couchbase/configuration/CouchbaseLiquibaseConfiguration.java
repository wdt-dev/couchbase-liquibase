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
    public static final ConfigurationDefinition<String> COUCHBASE_URL;
    public static final ConfigurationDefinition<String> COUCHBASE_USERNAME;
    public static final ConfigurationDefinition<String> COUCHBASE_PASSWORD;

    public static final ConfigurationDefinition<Duration> CHANGELOG_WAIT_TIME;
    public static final ConfigurationDefinition<Duration> CHANGELOG_RECHECK_TIME;
    public static final ConfigurationDefinition<Duration> LOCK_TTL;
    public static final ConfigurationDefinition<Duration> LOCK_TTL_PROLONGATION;
    public static final ConfigurationDefinition<Duration> TRANSACTION_TIMEOUT;
    public static final ConfigurationDefinition<Duration> MUTATE_IN_TIMEOUT;

    static {
        ConfigurationDefinition.Builder builder = new ConfigurationDefinition.Builder("liquibase.couchbase");

        CHANGELOG_RECHECK_TIME = builder.define("changelogRecheckTime", Duration.class)
                .addAliasKey("lockservice.changelogRecheckTime")
                .setDescription("Change log recheck time")
                .setDefaultValue(Duration.ofSeconds(10L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        CHANGELOG_WAIT_TIME = builder.define("changelogWaitTime", Duration.class)
                .addAliasKey("lockservice.changelogWaitTime")
                .setDescription("Time limit to wait for lock in LockService")
                .setDefaultValue(Duration.ofSeconds(300L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        CHANGELOG_LOCK_COLLECTION_NAME = builder.define("changelogCollectionName", String.class)
                .addAliasKey("changelogCollectionName")
                .setDescription("Collection name in service bucket")
                .setDefaultValue("CHANGELOGLOCKS")
                .build();

        SERVICE_BUCKET_NAME = builder.define("serviceBucketName", String.class)
                .addAliasKey("serviceBucketName")
                .setDescription("Liquibase service bucket name")
                .setDefaultValue("liquibaseServiceBucket")
                .build();

        LOCK_TTL_PROLONGATION = builder.define("ttlProlongation", Duration.class)
                .addAliasKey("lockservice.ttlProlongation")
                .setDescription("Liquibase locks prolongation time")
                .setDefaultValue(Duration.ofMinutes(1L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        LOCK_TTL = builder.define("lockTtl", Duration.class)
                .addAliasKey("lockservice.lockTtl")
                .setDescription("Liquibase locks time to live")
                .setDefaultValue(Duration.ofMinutes(3L))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        TRANSACTION_TIMEOUT = builder.define("transactionTimeout", Duration.class)
                .addAliasKey("transaction.timeout")
                .setDescription("Transactions timeout")
                .setDefaultValue(Duration.ofSeconds(15))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        MUTATE_IN_TIMEOUT = builder.define("mutateInTimeout", Duration.class)
                .addAliasKey("mutateIn.timeout")
                .setDescription("MutateIn operation timeout")
                .setDefaultValue(Duration.ofSeconds(2))
                .setValueHandler(CouchbaseLiquibaseConfiguration::durationExtract)
                .build();

        COUCHBASE_URL = builder.define("url", String.class)
                .addAliasKey("url")
                .setDescription("Liquibase url")
                .setDefaultValue("couchbase://127.0.0.1")
                .build();

        COUCHBASE_USERNAME = builder.define("username", String.class)
                .addAliasKey("username")
                .setDescription("Liquibase connection username")
                .setDefaultValue("Administrator")
                .build();


        COUCHBASE_PASSWORD = builder.define("password", String.class)
                .addAliasKey("password")
                .setDescription("Liquibase connection password")
                .setDefaultValue("password")
                .build();
    }

    private static Duration durationExtract(Object value) {
        return Optional.ofNullable(value)
                .map(String::valueOf)
                .map(Duration::parse)
                .orElse(null);
    }
}
