package com.havel.exception;

public class HavelException extends RuntimeException {

	private static final long serialVersionUID = 6167750803341902405L;

	public HavelException() {
		super();
	}

	public HavelException(String message, Throwable cause) {
		super(message, cause);
	}

	public HavelException(String message) {
		super(message);
	}

	public HavelException(Throwable cause) {
		super(cause);
	}

}
