package com.alessandragodoy.transactionms.utility;

import com.alessandragodoy.transactionms.controller.dto.DepositRequestDTO;
import com.alessandragodoy.transactionms.controller.dto.TransactionDTO;
import com.alessandragodoy.transactionms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.transactionms.controller.dto.WithdrawRequestDTO;
import com.alessandragodoy.transactionms.model.Transaction;
import com.alessandragodoy.transactionms.model.TransactionStatus;
import com.alessandragodoy.transactionms.model.TransactionType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Mapper class for converting between Transaction entities and various DTOs.
 */
@Component
public class TransactionMapper {

	public TransactionDTO toTransactionDTO(Transaction transaction) {
		return Optional.ofNullable(transaction)
				.map(t -> new TransactionDTO(
						t.getTransactionId(),
						t.getAccountId() != null ? t.getAccountId().toString() : null,
						t.getTransactionType().name(),
						t.getAmount(),
						t.getTransactionDate() != null ? t.getTransactionDate().toString() : null,
						t.getAccountId() != null ? t.getAccountId().toString() : null,
						t.getRelatedAccountId() != null ? t.getRelatedAccountId().toString() : null
				))
				.orElse(null);
	}

	public Transaction toDepositRequest(DepositRequestDTO depositRequestDTO) {
		return Optional.ofNullable(depositRequestDTO)
				.map(dto -> Transaction.builder()
						.transactionId(null)
						.transactionType(TransactionType.TRANSFER_OWN_ACCOUNT)
						.accountId(Integer.parseInt(dto.destinationAccount()))
						.relatedAccountId(null)
						.amount(dto.amount())
						.transactionDate(LocalDateTime.now())
						.status(TransactionStatus.PENDING)
						.build())
				.orElse(null);
	}

	public Transaction toWithdrawRequest(WithdrawRequestDTO withdrawRequestDTO) {
		return Optional.ofNullable(withdrawRequestDTO)
				.map(dto -> Transaction.builder()
						.transactionId(null)
						.transactionType(TransactionType.TRANSFER_OWN_ACCOUNT)
						.accountId(Integer.parseInt(dto.originAccount()))
						.relatedAccountId(null)
						.amount(dto.amount())
						.transactionDate(LocalDateTime.now())
						.status(TransactionStatus.PENDING)
						.build())
				.orElse(null);
	}

	public Transaction toTransferRequest(TransferRequestDTO transferRequestDTO) {
		return Optional.ofNullable(transferRequestDTO)
				.map(dto -> Transaction.builder()
						.transactionId(null)
						.transactionType(TransactionType.TRANSFER_INTER_ACCOUNT)
						.accountId(Integer.parseInt(dto.originAccount()))
						.relatedAccountId(Integer.parseInt(dto.destinationAccount()))
						.amount(dto.amount())
						.transactionDate(LocalDateTime.now())
						.status(TransactionStatus.PENDING)
						.build())
				.orElse(null);
	}
}
