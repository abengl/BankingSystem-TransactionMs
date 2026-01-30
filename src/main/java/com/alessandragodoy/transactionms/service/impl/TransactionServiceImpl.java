package com.alessandragodoy.transactionms.service.impl;

import com.alessandragodoy.transactionms.adapter.AccountServiceClient;
import com.alessandragodoy.transactionms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.transactionms.exception.TransactionNotFoundException;
import com.alessandragodoy.transactionms.exception.TransferFailedException;
import com.alessandragodoy.transactionms.model.Transaction;
import com.alessandragodoy.transactionms.model.TransactionStatus;
import com.alessandragodoy.transactionms.model.TransactionType;
import com.alessandragodoy.transactionms.repository.TransactionRepository;
import com.alessandragodoy.transactionms.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of the TransactionService interface.
 * This service handles the business logic for managing transactions.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final AccountServiceClient accountServiceClient;

	@Override
	public Flux<Transaction> getAllTransactions() {

		return transactionRepository.findAll();
	}

	@Override
	public Mono<Transaction> getTransactionById(String transactionId) {

		return transactionRepository.findById(transactionId)
				.switchIfEmpty(Mono.error(
						new TransactionNotFoundException(
								"Transaction not found with id: " + transactionId)));
	}

	@Override
	public Flux<Transaction> getTransactionsByAccountId(Integer accountId) {

		return transactionRepository.findByAccountId(accountId)
				.switchIfEmpty(Flux.error(
						new TransactionNotFoundException(
								"No transactions found for account id: " + accountId)));
	}

	@Override
	public Mono<Transaction> registerTransfer(TransferRequestDTO transfer) {

		Transaction transaction = Transaction.builder()
				.transactionType(TransactionType.valueOf(transfer.getTransactionType()))
				.accountId(transfer.getSourceAccountId())
				.relatedAccountId(transfer.getDestinationAccountId())
				.amount(transfer.getAmount())
				.status(TransactionStatus.PENDING)
				.build();

		return accountServiceClient.transfer(transfer)
				.flatMap(result -> {
					transaction.setStatus(result.getSuccess()
							? TransactionStatus.COMPLETED
							: TransactionStatus.FAILED);
					return transactionRepository.save(transaction)
							.flatMap(savedTransaction -> result.getSuccess() ?
									Mono.just(savedTransaction) :
									Mono.error(new TransferFailedException(
											result.getErrorCode() + " - " + result.getErrorMessage())));
				});

	}
}
