package com.alessandragodoy.transactionms;


import com.alessandragodoy.transactionms.adapter.TransactionAdapter;
import com.alessandragodoy.transactionms.controller.dto.DepositRequestDTO;
import com.alessandragodoy.transactionms.controller.dto.TransactionDTO;
import com.alessandragodoy.transactionms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.transactionms.controller.dto.WithdrawRequestDTO;
import com.alessandragodoy.transactionms.model.Transaction;
import com.alessandragodoy.transactionms.model.TransactionStatus;
import com.alessandragodoy.transactionms.model.TransactionType;
import com.alessandragodoy.transactionms.repository.TransactionRepository;
import com.alessandragodoy.transactionms.service.impl.TransactionServiceImpl;
import com.alessandragodoy.transactionms.utility.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	List<TransactionDTO> transactions = new ArrayList<>();
	@Mock
	private TransactionRepository transactionRepository;
	@Mock
	private TransactionMapper transactionMapper;
	@Mock
	private TransactionAdapter transactionAdapter;
	@InjectMocks
	private TransactionServiceImpl transactionService;

	@BeforeEach
	void setUp() {
		transactions.add(new TransactionDTO(
				"000001",
				"1",
				"TRANSFER_OWN_ACCOUNT",
				100.0,
				"2024-12-03T17:53:44.238",
				null,
				"2"
		));
		transactions.add(new TransactionDTO(
				"000002",
				"2",
				"TRANSFER_INTER_ACCOUNT",
				200.0,
				"2024-11-28T17:54:09.021",
				"2",
				"3"
		));
		transactions.add(new TransactionDTO(
				"000003",
				"3",
				"TRANSFER_OWN_ACCOUNT",
				300.0,
				"2024-11-28T17:53:44.238",
				"3",
				"1"
		));
	}

	@Test
	@DisplayName("Test listAllTransactions - Success operation")
	void listAllTransactions_Success() {
		Transaction transaction = Transaction.builder()
				.transactionId("000004")
				.transactionType(TransactionType.TRANSFER_OWN_ACCOUNT)
				.accountId(1)
				.relatedAccountId(2)
				.amount(100.0)
				.transactionDate(LocalDateTime.now())
				.status(TransactionStatus.COMPLETED)
				.build();

		when(transactionRepository.findAll()).thenReturn(Flux.just(transaction));
		when(transactionMapper.toTransactionDTO(transaction)).thenReturn(transactions.get(0));

		StepVerifier.create(transactionService.listAllTransactions())
				.expectNext(transactions.get(0))
				.verifyComplete();

		verify(transactionRepository).findAll();
		verify(transactionMapper).toTransactionDTO(transaction);
	}

	@Test
	@DisplayName("Test registerDeposit - Success operation")
	void registerDeposit_Success() {
		DepositRequestDTO depositRequest = new DepositRequestDTO("1", 100.0);
		Transaction transaction = Transaction.builder()
				.transactionId("000001")
				.transactionType(TransactionType.TRANSFER_OWN_ACCOUNT)
				.accountId(1)
				.relatedAccountId(null)
				.amount(100.0)
				.transactionDate(LocalDateTime.now())
				.status(TransactionStatus.COMPLETED)
				.build();

		when(transactionAdapter.verifyAccountNumber(
				depositRequest.destinationAccount())).thenReturn(Mono.just(true));
		when(transactionMapper.toDepositRequest(depositRequest)).thenReturn(transaction);
		when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
		when(transactionAdapter.updateAccountBalance("1", 100.0)).thenReturn(Mono.empty());
		when(transactionMapper.toTransactionDTO(transaction)).thenReturn(transactions.get(0));

		StepVerifier.create(transactionService.registerDeposit(depositRequest))
				.expectNext(transactions.get(0))
				.verifyComplete();

		verify(transactionAdapter).verifyAccountNumber("1");
		verify(transactionRepository).save(transaction);
		verify(transactionAdapter).updateAccountBalance("1", 100.0);
		verify(transactionMapper).toTransactionDTO(transaction);
	}

	@Test
	@DisplayName("Test registerWithdraw - Success operation")
	void registerWithdraw_Success() {
		WithdrawRequestDTO withdrawRequest = new WithdrawRequestDTO("1", 100.0);
		Transaction transaction = Transaction.builder()
				.transactionId("000002")
				.transactionType(TransactionType.TRANSFER_INTER_ACCOUNT)
				.accountId(1)
				.relatedAccountId(null)
				.amount(100.0)
				.transactionDate(LocalDateTime.now())
				.status(TransactionStatus.COMPLETED)
				.build();

		when(transactionAdapter.verifyAccountNumber(withdrawRequest.originAccount())).thenReturn(
				Mono.just(true));
		when(transactionAdapter.getAccountBalance(withdrawRequest.originAccount())).thenReturn(
				Mono.just(1000.0));
		when(transactionMapper.toWithdrawRequest(withdrawRequest)).thenReturn(transaction);
		when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
		when(transactionAdapter.updateAccountBalance("1", -100.0)).thenReturn(Mono.empty());
		when(transactionMapper.toTransactionDTO(transaction)).thenReturn(transactions.get(1));

		StepVerifier.create(transactionService.registerWithdraw(withdrawRequest))
				.expectNext(transactions.get(1))
				.verifyComplete();

		verify(transactionAdapter).verifyAccountNumber("1");
		verify(transactionAdapter).getAccountBalance("1");
		verify(transactionRepository).save(transaction);
		verify(transactionAdapter).updateAccountBalance("1", -100.0);
		verify(transactionMapper).toWithdrawRequest(withdrawRequest);
		verify(transactionMapper).toTransactionDTO(transaction);
	}

	@Test
	@DisplayName("Test registerTransfer - Success operation")
	void registerTransfer_Success() {
		TransferRequestDTO transferRequest = new TransferRequestDTO("1", "2", 100.0);
		Transaction transaction = Transaction.builder()
				.transactionId("000003")
				.transactionType(TransactionType.TRANSFER_INTER_ACCOUNT)
				.accountId(1)
				.relatedAccountId(2)
				.amount(100.0)
				.transactionDate(LocalDateTime.now())
				.status(TransactionStatus.COMPLETED)
				.build();

		when(transactionAdapter.verifyAccountNumber(transferRequest.originAccount())).thenReturn(
				Mono.just(true));
		when(transactionAdapter.verifyAccountNumber(
				transferRequest.destinationAccount())).thenReturn(Mono.just(true));
		when(transactionAdapter.getAccountBalance(transferRequest.originAccount())).thenReturn(
				Mono.just(1000.0));
		when(transactionMapper.toTransferRequest(transferRequest)).thenReturn(transaction);
		when(transactionRepository.save(transaction)).thenReturn(Mono.just(transaction));
		when(transactionAdapter.updateAccountBalance("1", -100.0)).thenReturn(Mono.empty());
		when(transactionAdapter.updateAccountBalance("2", 100.0)).thenReturn(Mono.empty());
		when(transactionMapper.toTransactionDTO(transaction)).thenReturn(transactions.get(2));

		StepVerifier.create(transactionService.registerTransfer(transferRequest))
				.expectNext(transactions.get(2))
				.verifyComplete();

		verify(transactionAdapter).verifyAccountNumber("1");
		verify(transactionAdapter).verifyAccountNumber("2");
		verify(transactionAdapter).getAccountBalance("1");
		verify(transactionRepository).save(transaction);
		verify(transactionAdapter).updateAccountBalance("1", -100.0);
		verify(transactionAdapter).updateAccountBalance("2", 100.0);
		verify(transactionMapper).toTransferRequest(transferRequest);
		verify(transactionMapper).toTransactionDTO(transaction);
	}

}

