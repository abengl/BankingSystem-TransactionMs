package com.alessandragodoy.transactionms.exception;

/**
 * Exception thrown when a transfer operation fails.
 */
public class TransferFailedException extends RuntimeException {
	public TransferFailedException(String message) {
		super(message);
	}
}
