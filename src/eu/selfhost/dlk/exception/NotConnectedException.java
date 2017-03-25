package eu.selfhost.dlk.exception;

public class NotConnectedException extends Exception {

	private static final long serialVersionUID = -7809184880866428571L;

	public NotConnectedException(String message) {
		super(message);
	}

	public NotConnectedException(Throwable cause) {
		super(cause);
	}

	public NotConnectedException(String message, Throwable cause) {
		super(message, cause);
	}
}
