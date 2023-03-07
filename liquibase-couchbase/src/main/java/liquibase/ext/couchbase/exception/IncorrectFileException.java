package liquibase.ext.couchbase.exception;

import static java.lang.String.format;

public class IncorrectFileException extends RuntimeException {

    private static final String template = "File [%s] format incorrect";

    public IncorrectFileException(String filePath) {
        super(format(template, filePath));
    }
}
