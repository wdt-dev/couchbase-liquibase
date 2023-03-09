package liquibase.ext.couchbase.parser;

import liquibase.Scope;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.ext.couchbase.exception.InvalidJSONException;
import liquibase.logging.Logger;
import liquibase.resource.ResourceAccessor;

import static java.lang.String.format;

public class JsonChangeLogParser extends liquibase.parser.core.json.JsonChangeLogParser {

    private final Logger log = Scope.getCurrentScope().getLog(getClass());

    public DatabaseChangeLog parse(String physicalChangeLogLocation,
                                   ChangeLogParameters changeLogParameters,
                                   ResourceAccessor resourceAccessor) throws ChangeLogParseException {
        log.info(format("Starting to parse [%s] changelog file", physicalChangeLogLocation));
        validateChangeLogFile(physicalChangeLogLocation);
        return super.parse(physicalChangeLogLocation, changeLogParameters, resourceAccessor);
    }

    private void validateChangeLogFile(String physicalChangeLogLocation) throws InvalidJSONException {
        // TODO validate file via json schema
    }
}
