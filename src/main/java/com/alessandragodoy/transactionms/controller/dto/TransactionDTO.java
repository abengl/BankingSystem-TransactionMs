package com.alessandragodoy.transactionms.controller.dto;

import com.alessandragodoy.transactionms.model.TransactionStatus;
import com.alessandragodoy.transactionms.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A Data Transfer Object for transactions.
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

	@Schema(description = "Unique identifier for the transaction", example =
			"6971004f05c9a61e36766dfa")
	String transactionId;

	@Schema(description = "Type of the transaction", example = "TRANSFER_OWN_ACCOUNT")
	TransactionType transactionType;

	@Schema(description = "Primary account ID", example = "1")
	Integer accountId;

	@Schema(description = "Destination account", example = "2")
	Integer relatedAccountId;

	@Schema(description = "Amount of the transaction", example = "100.0")
	Double amount;

	@Schema(description = "The transacion state", example = "COMPLETED")
	TransactionStatus transactionStatus;
}
