package com.alessandragodoy.transactionms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponseDTO {

	@Schema(description = "Indicates if the transfer was successful", example = "true")
	private Boolean success;

	@Schema(description = "Error code in case of a failed transfer", example =
			"INSUFFICIENT_FUNDS")
	private String errorCode;

	@Schema(description = "Error message in case of a failed transfer", example = "The source " +
			"account has insufficient funds.")
	private String errorMessage;

	@Schema(description = "Unique identifier for the source account", example = "1")
	private Integer sourceAccountId;

	@Schema(description = "Unique identifier for the destination account", example = "2")
	private Integer destinationAccountId;

	@Schema(description = "Final balance of the source account after the transfer", example = "900" +
			".0")
	private Double finalSourceBalance;

	@Schema(description = "Final balance of the destination account after the transfer", example =
			"1100.0")
	private Double finalDestinationBalance;

}
