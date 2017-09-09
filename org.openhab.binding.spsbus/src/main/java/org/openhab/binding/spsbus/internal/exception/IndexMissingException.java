package org.openhab.binding.spsbus.internal.exception;

public class IndexMissingException extends Exception {

    private static final long serialVersionUID = 7620780437792012873L;

    public IndexMissingException(String message) {
        super(message);
    }

    public IndexMissingException(Throwable cause) {
        super(cause);
    }

    public IndexMissingException(String message, Throwable cause) {
        super(message, cause);
    }

}
