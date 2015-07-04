package com.smeanox.games.sg002.player;

/**
 * An AI didn't follow protocol
 *
 * @author Benjamin Schmid
 */
public class ProtocolViolationException extends Exception {
	public ProtocolViolationException() {
		super();
	}

	public ProtocolViolationException(String message) {
		super(message);
	}

	public ProtocolViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolViolationException(Throwable cause) {
		super(cause);
	}

	protected ProtocolViolationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
