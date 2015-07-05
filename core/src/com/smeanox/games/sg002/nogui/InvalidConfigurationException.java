package com.smeanox.games.sg002.nogui;

/**
 * The configuration is invalid
 *
 * @author Benjamin Schmid
 */
public class InvalidConfigurationException extends Exception {
	public InvalidConfigurationException() {
		super();
	}

	public InvalidConfigurationException(String message) {
		super(message);
	}

	public InvalidConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidConfigurationException(Throwable cause) {
		super(cause);
	}

	protected InvalidConfigurationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
