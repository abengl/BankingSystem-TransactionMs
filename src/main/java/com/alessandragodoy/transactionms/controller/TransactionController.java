package com.alessandragodoy.transactionms.controller;

import com.alessandragodoy.transactionms.controller.dto.TransactionDTO;
import com.alessandragodoy.transactionms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.transactionms.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.alessandragodoy.transactionms.utility.DTOMapper.convertToDTO;

/**
 * Controller for handling transaction-related requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction", description = "Endpoints for managing transactions")
public class TransactionController {
	private final TransactionService transactionService;

	/**
	 * Retrieves all transactions registered.
	 *
	 * @return {@code ResponseEntity<Flux<TransactionDTO>>} a transactions list
	 */
	@Operation(summary = "Retrieve all transactions", description = "Returns the list of transactions.")
	@GetMapping
	public Mono<ResponseEntity<Flux<TransactionDTO>>> getAllTransactions() {

		return transactionService.getAllTransactions()
				.map(transaction -> convertToDTO(transaction, TransactionDTO.class))
				.collectList()
				.map(transactions -> ResponseEntity.ok(Flux.fromIterable(transactions)));
	}

	/**
	 * Retrieves a transaction by its ID.
	 *
	 * @param transactionId the unique identifier of the transaction
	 * @return {@code ResponseEntity<TransactionDTO>} the transaction details
	 */
	@Operation(summary = "Retrieve a transaction by ID", description = "Returns the details of " +
			"a specific transaction by its unique identifier.")
	@GetMapping("/{transactionId}")
	public Mono<ResponseEntity<TransactionDTO>> getTransactionById(
			@PathVariable String transactionId) {

		return transactionService.getTransactionById(transactionId)
				.map(transaction -> ResponseEntity.ok(
						convertToDTO(transaction, TransactionDTO.class)));
	}

	/**
	 * Retrieves transactions by account ID.
	 *
	 * @param accountId the unique identifier of the account
	 * @return {@code ResponseEntity<Flux<TransactionDTO>>} a list of transactions for the account
	 */
	@Operation(summary = "Retrieve transactions by account ID", description = "Returns a list " +
			"of transactions associated with a specific account ID.")
	@GetMapping("/account/{accountId}")
	public Mono<ResponseEntity<Flux<TransactionDTO>>> getTransactionsByAccountId(
			@PathVariable Integer accountId) {

		return transactionService.getTransactionsByAccountId(accountId)
				.map(transaction -> convertToDTO(transaction, TransactionDTO.class))
				.collectList()
				.map(transactions -> ResponseEntity.ok(Flux.fromIterable(transactions)));
	}

	/**
	 * Registers a transfer transaction.
	 *
	 * @param transferRequest the transfer request data transfer object
	 * @return {@code ResponseEntity<TransactionDTO>} containing the TransactionDTO
	 */
	@Operation(summary = "Register a transfer transaction", description = "Returns the " +
			"transaction" +
			" details after registering a transfer.")
	@PostMapping("/transfer")
	public Mono<ResponseEntity<TransactionDTO>> registerTransfer(
			@Valid @RequestBody TransferRequestDTO transferRequest) {

		return transactionService.registerTransfer(transferRequest)
				.map(transaction -> ResponseEntity
						.status(HttpStatus.CREATED).body(
								convertToDTO(transaction, TransactionDTO.class)));
	}

}
