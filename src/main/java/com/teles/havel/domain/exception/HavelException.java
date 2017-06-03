package com.teles.havel.domain.exception;

public class HavelException extends RuntimeException {

	private static final long serialVersionUID = 1L;

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
