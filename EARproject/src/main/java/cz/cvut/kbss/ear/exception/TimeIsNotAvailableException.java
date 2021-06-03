package cz.cvut.kbss.ear.exception;

public class TimeIsNotAvailableException extends EarException {
    public TimeIsNotAvailableException(String message) {
        super(message);
    }

    public static TimeIsNotAvailableException create(String resourceName, Object identifier) {
        return new TimeIsNotAvailableException(resourceName + " identified by " + identifier + " is not available");
    }
}
