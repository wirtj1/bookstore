package org.books.application.exception;

public class PaymentFailedException extends BookstoreException {

	public enum Code {

		INVALID_CREDIT_CARD, CREDIT_CARD_EXPIRED, PAYMENT_LIMIT_EXCEEDED,
	}

	private final Code code;

	public PaymentFailedException(Code code) {
		this.code = code;
	}

	public Code getCode() {
		return code;
	}
}
