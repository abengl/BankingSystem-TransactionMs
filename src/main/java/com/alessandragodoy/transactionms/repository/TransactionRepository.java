package com.alessandragodoy.transactionms.repository;

import com.alessandragodoy.transactionms.model.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Repository interface for managing `Transaction` entities in MongoDB.
 */
@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String> {

	/**
	 * Find transactions by account ID.
	 *
	 * @param accountId the account ID
	 * @return a Flux of Transactions associated with the given account ID
	 */
	Flux<Transaction> findByAccountId(Integer accountId);
}
