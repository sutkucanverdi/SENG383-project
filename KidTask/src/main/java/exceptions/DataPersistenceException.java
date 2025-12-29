package exceptions;

/**
 * Signals a failure while reading or writing backing CSV files.
 */
public class DataPersistenceException extends RuntimeException {
    public DataPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataPersistenceException(String message) {
        super(message);
    }
}

