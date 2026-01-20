package com.alessandragodoy.transactionms.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * Represents a transaction in the banking system.
 * This class is used to store transaction details such as account information, transaction type,
 * amount, and date.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Document(collection = "transaction")
public class Transaction {

	@Id
	@EqualsAndHashCode.Include
	private String transactionId;

	@Field("transaction_type")
	private TransactionType transactionType;

	@Field("account_id")
	private Integer accountId;

	@Field("related_account_id")
	private Integer relatedAccountId;

	@Field("amount")
	private double amount;

	@CreatedDate
	@Field("transaction_date")
	private LocalDateTime transactionDate;

	@Field("status")
	private TransactionStatus status;
}
