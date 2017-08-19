package org.openhab.binding.spsbus.internal.exception;

/**
 *
 * @author David
 *
 */
public class InvalidPacketException extends Exception {

    private static final long serialVersionUID = 2329888129618281640L;

    public InvalidPacketException(String message) {
        super(message);
    }

    public InvalidPacketException(Throwable cause) {
        super(cause);
    }

    public InvalidPacketException(String message, Throwable cause) {
        super(message, cause);
    }

}
