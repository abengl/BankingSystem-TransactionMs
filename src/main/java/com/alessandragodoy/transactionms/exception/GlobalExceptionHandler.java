package com.alessandragodoy.transactionms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Global exception handler for handling custom exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles TransactionNotFoundException.
	 *
	 * @param ex       the exception that was thrown
	 * @param exchange the server web exchange containing request details
	 * @return a Mono of ResponseEntity with NOT FOUND status and exception message.
	 */
	@ExceptionHandler(TransactionNotFoundException.class)
	public Mono<ResponseEntity<CustomErrorResponse>> handleTransactionNotFoundException(
			TransactionNotFoundException ex, ServerWebExchange exchange) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				exchange.getRequest().getPath().value());

		return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(err));
	}

	/**
	 * Handles AccountNotFoundException.
	 *
	 * @param ex       the exception
	 * @param exchange the server web exchange containing request details
	 * @return the response entity with NOT FOUND status and exception message.
	 */
	@ExceptionHandler(AccountNotFoundException.class)
	public Mono<ResponseEntity<CustomErrorResponse>> handleAccountNotFoundException(
			AccountNotFoundException ex, ServerWebExchange exchange) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				exchange.getRequest().getPath().value());

		return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(err));
	}

	/**
	 * Handles InsufficientFundsException.
	 *
	 * @param ex       the exception
	 * @param exchange the server web exchange containing request details
	 * @return the response entity with CONFLICT status and exception message
	 */
	@ExceptionHandler(InsufficientFundsException.class)
	public Mono<ResponseEntity<CustomErrorResponse>> handleInsufficientFundsException(
			InsufficientFundsException ex, ServerWebExchange exchange) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				exchange.getRequest().getPath().value());

		return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(err));
	}

	/**
	 * Handles InvalidParameterException.
	 *
	 * @param ex       the exception
	 * @param exchange the server web exchange containing request details
	 * @return the response entity with BAD REQUEST status and exception message.
	 */
	@ExceptionHandler(InvalidParameterException.class)
	public Mono<ResponseEntity<CustomErrorResponse>> handleInvalidParameterException(
			InvalidParameterException ex, ServerWebExchange exchange) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				exchange.getRequest().getPath().value());

		return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err));
	}

	/**
	 * Handles WebExchangeBindException.
	 *
	 * @param ex       the exception
	 * @param exchange the server web exchange containing request details
	 * @return the response entity with BAD REQUEST status and exception message.
	 */
	@ExceptionHandler(WebExchangeBindException.class)
	public Mono<ResponseEntity<CustomErrorResponse>> handleMethodArgumentNotValidException(
			WebExchangeBindException ex, ServerWebExchange exchange) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				exchange.getRequest().getPath().value());

		return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err));
	}

	/**
	 * Handles ExternalServiceException.
	 *
	 * @param ex       the exception
	 * @param exchange the server web exchange containing request details
	 * @return the response entity with SERVICE UNAVAILABLE status and exception message.
	 */
	@ExceptionHandler(ExternalServiceException.class)
	public Mono<ResponseEntity<CustomErrorResponse>> handleExternalServiceException(
			ExternalServiceException ex, ServerWebExchange exchange) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				exchange.getRequest().getPath().value());

		return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(err));
	}

	/**
	 * Handles TransferFailedException.
	 *
	 * @param ex       the exception
	 * @param exchange the server web exchange containing request details
	 * @return the response entity with UNPROCESSABLE ENTITY status and exception message.
	 */
	@ExceptionHandler(TransferFailedException.class)
	public Mono<ResponseEntity<CustomErrorResponse>> handleTransferFailedException(
			TransferFailedException ex, ServerWebExchange exchange) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				exchange.getRequest().getPath().value());

		return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(err));
	}

	/**
	 * Handles all uncaught exceptions and returns a 500 Internal Server Error response.
	 *
	 * @param ex       the exception
	 * @param exchange the server web exchange containing request details
	 * @return a ResponseEntity with a 500 status and a custom error response.
	 */
	@ExceptionHandler(Exception.class)
	public Mono<ResponseEntity<CustomErrorResponse>> handleDefaultException(Exception ex,
																			ServerWebExchange exchange) {

		CustomErrorResponse err = new CustomErrorResponse(LocalDateTime.now(), ex.getMessage(),
				exchange.getRequest().getPath().value());

		return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err));
	}
}
