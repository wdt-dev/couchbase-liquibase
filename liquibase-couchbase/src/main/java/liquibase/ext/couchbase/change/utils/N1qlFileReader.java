package liquibase.ext.couchbase.change.utils;

import com.google.common.io.CharStreams;
import liquibase.exception.ChangeLogParseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.Resource;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.InputStreamReader;

public final class N1qlFileReader {
    private static final ClassLoaderResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();

    @SneakyThrows
    public static String load(String physicalChangeLogLocation) {
        Resource changelog = resourceAccessor.get(physicalChangeLogLocation);
        if (!changelog.exists()) {
            throw new ChangeLogParseException(physicalChangeLogLocation + " does not exist");
        }
        else {
            try (InputStream changeLogStream = changelog.openInputStream()) {
                return CharStreams.toString(new InputStreamReader(changeLogStream));
            }
        }
    }
}
