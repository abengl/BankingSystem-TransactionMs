package com.alessandragodoy.transactionms.controller.dto;

import com.alessandragodoy.transactionms.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transactions requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {


	@NotNull()
	@Pattern(regexp = "^(TRANSFER_OWN_ACCOUNT|TRANSFER_THIRD_PARTY_ACCOUNT)$", message =
			"Transaction type must be either " +
					"'TRANSFER_OWN_ACCOUNT' or 'TRANSFER_THIRD_PARTY_ACCOUNT'")
	@Schema(description = "Type of the transaction", example = "TRANSFER_OWN_ACCOUNT")
	TransactionType transactionType;

	@NotNull()
	@Positive()
	@Schema(description = "Unique identifier for the source account", example = "1")
	Integer sourceAccountId;

	@NotNull()
	@Positive()
	@Schema(description = "Unique identifier for the destination account", example = "2")
	Integer destinationAccountId;

	@NotNull()
	@Positive()
	@Schema(description = "Amount to deposit", example = "100.0")
	Double amount;

}
