package com.alessandragodoy.transactionms.controller;

import com.alessandragodoy.transactionms.api.TransactionApi;
import com.alessandragodoy.transactionms.dto.TransactionDTO;
import com.alessandragodoy.transactionms.dto.TransferRequestDTO;
import com.alessandragodoy.transactionms.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.alessandragodoy.transactionms.utility.DTOMapper.convertToDTO;

/**
 * Controller for handling transaction-related requests.
 */
@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionApi {
	private final TransactionService transactionService;

	/**
	 * Retrieves all transactions registered.
	 *
	 * @param exchange the server web exchange
	 * @return {@code ResponseEntity<Flux<TransactionDTO>>} a transactions list
	 */
	public Mono<ResponseEntity<Flux<TransactionDTO>>> getAllTransactions(
			ServerWebExchange exchange) {

		return transactionService.getAllTransactions()
				.map(transaction -> convertToDTO(transaction, TransactionDTO.class))
				.collectList()
				.map(transactions -> ResponseEntity.ok(Flux.fromIterable(transactions)));
	}

	/**
	 * Retrieves a transaction by its ID.
	 *
	 * @param transactionId the unique identifier of the transaction
	 * @param exchange      the server web exchange
	 * @return {@code ResponseEntity<TransactionDTO>} the transaction details
	 */
	public Mono<ResponseEntity<TransactionDTO>> getTransactionById(
			@PathVariable String transactionId, ServerWebExchange exchange) {

		return transactionService.getTransactionById(transactionId)
				.map(transaction -> ResponseEntity.ok(
						convertToDTO(transaction, TransactionDTO.class)));
	}

	/**
	 * Retrieves transactions by account ID.
	 *
	 * @param accountId the unique identifier of the account
	 * @param exchange  the server web exchange
	 * @return {@code ResponseEntity<Flux<TransactionDTO>>} a list of transactions for the account
	 */
	public Mono<ResponseEntity<Flux<TransactionDTO>>> getTransactionsByAccountId(
			@PathVariable Integer accountId, ServerWebExchange exchange) {

		return transactionService.getTransactionsByAccountId(accountId)
				.map(transaction -> convertToDTO(transaction, TransactionDTO.class))
				.collectList()
				.map(transactions -> ResponseEntity.ok(Flux.fromIterable(transactions)));
	}

	/**
	 * Registers a transfer transaction.
	 *
	 * @param transferRequestDTO the transfer request data transfer object
	 * @param exchange           the server web exchange
	 * @return {@code ResponseEntity<TransactionDTO>} containing the TransactionDTO
	 */
	public Mono<ResponseEntity<TransactionDTO>> registerTransfer(
			@Valid @RequestBody Mono<TransferRequestDTO> transferRequestDTO,
			ServerWebExchange exchange) {

		return transferRequestDTO
				.flatMap(request -> transactionService.registerTransfer(request)
						.map(transaction -> ResponseEntity
								.status(HttpStatus.CREATED)
								.body(convertToDTO(transaction, TransactionDTO.class))));
	}

}
