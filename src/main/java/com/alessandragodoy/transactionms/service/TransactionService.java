package com.alessandragodoy.transactionms.service;

import com.alessandragodoy.transactionms.dto.TransferRequestDTO;
import com.alessandragodoy.transactionms.model.Transaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service interface for managing transactions in the banking system.
 */
public interface TransactionService {

	/**
	 * Lists all transactions.
	 *
	 * @return {@code Flux<Transaction>} representing all transactions.
	 */
	Flux<Transaction> getAllTransactions();

	/**
	 * Retrieves a transaction by its ID.
	 *
	 * @param transactionId the ID of the transaction to retrieve.
	 * @return {@code Mono<Transaction>} representing the retrieved transaction.
	 */
	Mono<Transaction> getTransactionById(String transactionId);

	/**
	 * Lists all transactions for a specific account ID.
	 *
	 * @param accountId the account ID to filter transactions.
	 * @return {@code Flux<Transaction>} representing the transactions for the specified account
	 * ID.
	 */
	Flux<Transaction> getTransactionsByAccountId(Integer accountId);

	/**
	 * Registers a transfer transaction.
	 *
	 * @param transfer the transfer request data transfer object.
	 * @return {@code Mono<Transaction>} representing the registered transfer transaction.
	 */
	Mono<Transaction> registerTransfer(TransferRequestDTO transfer);
}
