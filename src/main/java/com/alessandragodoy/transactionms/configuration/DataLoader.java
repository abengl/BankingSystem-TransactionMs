package com.alessandragodoy.transactionms.configuration;

import com.alessandragodoy.transactionms.model.Transaction;
import com.alessandragodoy.transactionms.model.TransactionStatus;
import com.alessandragodoy.transactionms.model.TransactionType;
import com.alessandragodoy.transactionms.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataLoader.class);
	private final TransactionRepository transactionRepository;

	@Override
	public void run(String... args) {
		transactionRepository.count()
				.flatMapMany(count -> {
					if (count == 0) {
						LOGGER.info("No transactions found, creating initial transactions...");
						List<Transaction> initialTransactions = List.of(
								Transaction.builder()
										.transactionId(null)
										.transactionType(TransactionType.TRANSFER_OWN_ACCOUNT)
										.accountId(1)
										.relatedAccountId(2)
										.amount(100.0)
										.status(TransactionStatus.COMPLETED)
										.build(),
								Transaction.builder()
										.transactionId(null)
										.transactionType(
												TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT)
										.accountId(3)
										.relatedAccountId(4)
										.amount(200.0)
										.status(TransactionStatus.COMPLETED)
										.build(),
								Transaction.builder()
										.transactionId(null)
										.transactionType(TransactionType.TRANSFER_OWN_ACCOUNT)
										.accountId(5)
										.relatedAccountId(1)
										.amount(300.0)
										.status(TransactionStatus.FAILED)
										.build()
						);
						return transactionRepository.saveAll(initialTransactions)
								.doOnComplete(() -> LOGGER.info(
										"Initial transactions added to the database."));
					} else {
						LOGGER.info("Transactions already exist, skipping initialization.");
						return Flux.empty();
					}
				})
				.subscribe();
	}
}
