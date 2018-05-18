package org.books.integration;

public class AmazonException extends RuntimeException {

	private final String code;

	public AmazonException(String message, String code) {
		super(message);
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return getClass().getName() + " (" + code + "): " + getMessage();
	}
}
