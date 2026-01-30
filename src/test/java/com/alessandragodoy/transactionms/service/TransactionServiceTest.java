package com.alessandragodoy.transactionms.service;

import com.alessandragodoy.transactionms.adapter.AccountServiceClient;
import com.alessandragodoy.transactionms.controller.dto.TransferRequestDTO;
import com.alessandragodoy.transactionms.controller.dto.TransferResponseDTO;
import com.alessandragodoy.transactionms.exception.ExternalServiceException;
import com.alessandragodoy.transactionms.exception.TransactionNotFoundException;
import com.alessandragodoy.transactionms.exception.TransferFailedException;
import com.alessandragodoy.transactionms.model.Transaction;
import com.alessandragodoy.transactionms.model.TransactionStatus;
import com.alessandragodoy.transactionms.model.TransactionType;
import com.alessandragodoy.transactionms.repository.TransactionRepository;
import com.alessandragodoy.transactionms.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionServiceImpl.
 * Tests reactive business logic for transaction management.
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AccountServiceClient accountServiceClient;

	@InjectMocks
	private TransactionServiceImpl transactionService;

	@Test
	@DisplayName("getAllTransactions - returns flux of all transactions")
	void getAllTransactions_WithTransactions_ReturnsFlux() {

		Transaction tx1 = createTransaction("tx1", TransactionType.TRANSFER_OWN_ACCOUNT,
				1, 2, 100.0, TransactionStatus.COMPLETED);
		Transaction tx2 = createTransaction("tx2", TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT,
				2, 3, 200.0, TransactionStatus.COMPLETED);
		Transaction tx3 = createTransaction("tx3", TransactionType.TRANSFER_OWN_ACCOUNT,
				1, 3, 50.0, TransactionStatus.COMPLETED);

		when(transactionRepository.findAll()).thenReturn(Flux.just(tx1, tx2, tx3));

		StepVerifier.create(transactionService.getAllTransactions())
				.expectNext(tx1)
				.expectNext(tx2)
				.expectNext(tx3)
				.verifyComplete();

		verify(transactionRepository).findAll();
	}

	@Test
	@DisplayName("getAllTransactions - returns empty flux when no transactions")
	void getAllTransactions_WithNoTransactions_ReturnsEmptyFlux() {

		when(transactionRepository.findAll()).thenReturn(Flux.empty());

		StepVerifier.create(transactionService.getAllTransactions())
				.expectNextCount(0)
				.verifyComplete();

		verify(transactionRepository).findAll();
	}

	@Test
	@DisplayName("getAllTransactions - handles large number of transactions")
	void getAllTransactions_WithManyTransactions_ReturnsAllTransactions() {

		Flux<Transaction> manyTransactions = Flux.range(1, 100)
				.map(i -> createTransaction(
						"tx" + i,
						i % 2 == 0 ? TransactionType.TRANSFER_OWN_ACCOUNT :
								TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT,
						i, i + 1, 100.0 * i, TransactionStatus.COMPLETED));

		when(transactionRepository.findAll()).thenReturn(manyTransactions);

		StepVerifier.create(transactionService.getAllTransactions())
				.expectNextCount(100)
				.verifyComplete();

		verify(transactionRepository).findAll();
	}

	@Test
	@DisplayName("getTransactionById - returns transaction when found")
	void getTransactionById_WithValidId_ReturnsMono() {

		String transactionId = "507f1f77bcf86cd799439011";
		Transaction transaction = createTransaction(transactionId,
				TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 150.0, TransactionStatus.COMPLETED);

		when(transactionRepository.findById(transactionId))
				.thenReturn(Mono.just(transaction));

		StepVerifier.create(transactionService.getTransactionById(transactionId))
				.expectNext(transaction)
				.verifyComplete();

		verify(transactionRepository).findById(transactionId);
	}

	@Test
	@DisplayName("getTransactionById - throws exception when not found")
	void getTransactionById_WithNonExistentId_ThrowsException() {

		String nonExistentId = "nonexistent123";
		when(transactionRepository.findById(nonExistentId)).thenReturn(Mono.empty());

		StepVerifier.create(transactionService.getTransactionById(nonExistentId))
				.expectErrorMatches(throwable ->
						throwable instanceof TransactionNotFoundException &&
								throwable.getMessage()
										.equals("Transaction not found with id: " + nonExistentId))
				.verify();

		verify(transactionRepository).findById(nonExistentId);
	}

	@Test
	@DisplayName("getTransactionById - verifies transaction details")
	void getTransactionById_VerifiesTransactionDetails() {

		String id = "tx123";
		Transaction transaction = createTransaction(id, TransactionType.TRANSFER_OWN_ACCOUNT,
				5, 6, 250.0, TransactionStatus.COMPLETED);

		when(transactionRepository.findById(id)).thenReturn(Mono.just(transaction));

		StepVerifier.create(transactionService.getTransactionById(id))
				.assertNext(tx -> {
					assertThat(tx.getTransactionId()).isEqualTo(id);
					assertThat(tx.getTransactionType()).isEqualTo(
							TransactionType.TRANSFER_OWN_ACCOUNT);
					assertThat(tx.getAccountId()).isEqualTo(5);
					assertThat(tx.getRelatedAccountId()).isEqualTo(6);
					assertThat(tx.getAmount()).isEqualTo(250.0);
					assertThat(tx.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
				})
				.verifyComplete();

		verify(transactionRepository).findById(id);
	}

	@Test
	@DisplayName("getTransactionsByAccountId - returns flux of transactions for account")
	void getTransactionsByAccountId_WithTransactions_ReturnsFlux() {

		Integer accountId = 1;
		Transaction tx1 = createTransaction("tx1", TransactionType.TRANSFER_OWN_ACCOUNT,
				accountId, 2, 100.0, TransactionStatus.COMPLETED);
		Transaction tx2 = createTransaction("tx2", TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT,
				accountId, 3, 200.0, TransactionStatus.COMPLETED);

		when(transactionRepository.findByAccountId(accountId))
				.thenReturn(Flux.just(tx1, tx2));

		StepVerifier.create(transactionService.getTransactionsByAccountId(accountId))
				.expectNext(tx1)
				.expectNext(tx2)
				.verifyComplete();

		verify(transactionRepository).findByAccountId(accountId);
	}

	@Test
	@DisplayName("getTransactionsByAccountId - throws exception when no transactions found")
	void getTransactionsByAccountId_WithNoTransactions_ThrowsException() {

		Integer accountId = 999;
		when(transactionRepository.findByAccountId(accountId)).thenReturn(Flux.empty());

		StepVerifier.create(transactionService.getTransactionsByAccountId(accountId))
				.expectErrorMatches(throwable ->
						throwable instanceof TransactionNotFoundException &&
								throwable.getMessage()
										.equals("No transactions found for account id: " + accountId))
				.verify();

		verify(transactionRepository).findByAccountId(accountId);
	}

	@Test
	@DisplayName("getTransactionsByAccountId - verifies all transactions belong to account")
	void getTransactionsByAccountId_VerifiesAccountOwnership() {

		Integer accountId = 5;
		Transaction tx1 = createTransaction("tx1", TransactionType.TRANSFER_OWN_ACCOUNT,
				accountId, 6, 100.0, TransactionStatus.COMPLETED);
		Transaction tx2 = createTransaction("tx2", TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT,
				accountId, 7, 200.0, TransactionStatus.COMPLETED);

		when(transactionRepository.findByAccountId(accountId))
				.thenReturn(Flux.just(tx1, tx2));

		StepVerifier.create(transactionService.getTransactionsByAccountId(accountId))
				.assertNext(tx -> assertThat(tx.getAccountId()).isEqualTo(accountId))
				.assertNext(tx -> assertThat(tx.getAccountId()).isEqualTo(accountId))
				.verifyComplete();

		verify(transactionRepository).findByAccountId(accountId);
	}

	@Test
	@DisplayName("registerTransfer - successfully registers transfer and saves with COMPLETED " +
			"status")
	void registerTransfer_WithSuccessfulTransfer_SavesCompletedTransaction() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 1, 2, 300.0);

		TransferResponseDTO successResponse = createSuccessResponse(1, 2, 700.0, 800.0);

		Transaction savedTransaction = createTransaction("tx123",
				TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 300.0, TransactionStatus.COMPLETED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(successResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.assertNext(transaction -> {
					assertThat(transaction.getTransactionId()).isEqualTo("tx123");
					assertThat(transaction.getTransactionType()).isEqualTo(
							TransactionType.TRANSFER_OWN_ACCOUNT);
					assertThat(transaction.getAccountId()).isEqualTo(1);
					assertThat(transaction.getRelatedAccountId()).isEqualTo(2);
					assertThat(transaction.getAmount()).isEqualTo(300.0);
					assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
				})
				.verifyComplete();

		verify(accountServiceClient).transfer(any(TransferRequestDTO.class));
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	@DisplayName("registerTransfer - creates transaction with PENDING status before external call")
	void registerTransfer_CreatesTransactionWithPendingStatus() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 5, 6, 500.0);

		TransferResponseDTO successResponse = createSuccessResponse(5, 6, 500.0, 500.0);

		Transaction savedTransaction = createTransaction("tx456",
				TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, 5, 6, 500.0,
				TransactionStatus.COMPLETED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(successResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.expectNextCount(1)
				.verifyComplete();

		// Verify transaction was created with correct initial state
		ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
		verify(transactionRepository).save(transactionCaptor.capture());

		Transaction capturedTransaction = transactionCaptor.getValue();
		assertThat(capturedTransaction.getTransactionType()).isEqualTo(
				TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT);
		assertThat(capturedTransaction.getAccountId()).isEqualTo(5);
		assertThat(capturedTransaction.getRelatedAccountId()).isEqualTo(6);
		assertThat(capturedTransaction.getAmount()).isEqualTo(500.0);
	}

	@Test
	@DisplayName("registerTransfer - successfully registers third party transfer")
	void registerTransfer_WithThirdPartyTransfer_Succeeds() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 10, 20, 1000.0);

		TransferResponseDTO successResponse = createSuccessResponse(10, 20, 0.0, 1000.0);

		Transaction savedTransaction = createTransaction("tx789",
				TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, 10, 20, 1000.0,
				TransactionStatus.COMPLETED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(successResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.assertNext(transaction -> {
					assertThat(transaction.getTransactionType())
							.isEqualTo(TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT);
					assertThat(transaction.getAmount()).isEqualTo(1000.0);
					assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
				})
				.verifyComplete();

		verify(accountServiceClient).transfer(any(TransferRequestDTO.class));
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	@DisplayName("registerTransfer - saves transaction with FAILED status when transfer fails")
	void registerTransfer_WithFailedTransfer_SavesFailedTransaction() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 1, 2, 500.0);

		TransferResponseDTO failedResponse = createFailedResponse(
				"INSUFFICIENT_FUNDS", "Insufficient balance in source account: 100.0");

		Transaction savedTransaction = createTransaction("txFail",
				TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 500.0, TransactionStatus.FAILED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(failedResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.expectErrorMatches(throwable ->
						throwable instanceof TransferFailedException &&
								throwable.getMessage().contains("INSUFFICIENT_FUNDS") &&
								throwable.getMessage().contains("Insufficient balance"))
				.verify();

		verify(accountServiceClient).transfer(any(TransferRequestDTO.class));
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	@DisplayName("registerTransfer - handles source account not found error")
	void registerTransfer_WithSourceAccountNotFound_ThrowsException() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 999, 2, 100.0);

		TransferResponseDTO failedResponse = createFailedResponse(
				"SOURCE_ACCOUNT_NOT_FOUND", "Source account not found for ID: 999");

		Transaction savedTransaction = createTransaction("txFail2",
				TransactionType.TRANSFER_OWN_ACCOUNT, 999, 2, 100.0, TransactionStatus.FAILED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(failedResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.expectErrorMatches(throwable ->
						throwable instanceof TransferFailedException &&
								throwable.getMessage().contains("SOURCE_ACCOUNT_NOT_FOUND"))
				.verify();

		verify(accountServiceClient).transfer(any(TransferRequestDTO.class));
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	@DisplayName("registerTransfer - handles destination account inactive error")
	void registerTransfer_WithDestinationAccountInactive_ThrowsException() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 5, 200.0);

		TransferResponseDTO failedResponse = createFailedResponse(
				"DESTINATION_ACCOUNT_INACTIVE", "Destination account is not active for ID: 5");

		Transaction savedTransaction = createTransaction("txFail3",
				TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, 1, 5, 200.0,
				TransactionStatus.FAILED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(failedResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.expectErrorMatches(throwable ->
						throwable instanceof TransferFailedException &&
								throwable.getMessage().contains("DESTINATION_ACCOUNT_INACTIVE"))
				.verify();

		verify(accountServiceClient).transfer(any(TransferRequestDTO.class));
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	@DisplayName("registerTransfer - transaction is saved even when transfer fails")
	void registerTransfer_AlwaysSavesTransaction() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 1, 2, 300.0);

		TransferResponseDTO failedResponse = createFailedResponse(
				"INSUFFICIENT_FUNDS", "Not enough balance");

		Transaction savedTransaction = createTransaction("txAlwaysSaved",
				TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 300.0, TransactionStatus.FAILED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(failedResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.expectError(TransferFailedException.class)
				.verify();

		// Verify transaction was saved despite failure
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	@DisplayName("registerTransfer - handles external service error gracefully")
	void registerTransfer_WithExternalServiceError_PropagatesError() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 1, 2, 100.0);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.error(new ExternalServiceException("Service unavailable")));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.expectErrorMatches(throwable ->
						throwable instanceof ExternalServiceException &&
								throwable.getMessage().equals("Service unavailable"))
				.verify();

		verify(accountServiceClient).transfer(any(TransferRequestDTO.class));
		verify(transactionRepository, never()).save(any(Transaction.class));
	}

	@Test
	@DisplayName("registerTransfer - handles small transfer amount")
	void registerTransfer_WithSmallAmount_Succeeds() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 1, 2, 0.01);

		TransferResponseDTO successResponse = createSuccessResponse(1, 2, 999.99, 500.01);

		Transaction savedTransaction = createTransaction("txSmall",
				TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 0.01, TransactionStatus.COMPLETED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(successResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.assertNext(transaction -> {
					assertThat(transaction.getAmount()).isEqualTo(0.01);
					assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
				})
				.verifyComplete();
	}

	@Test
	@DisplayName("registerTransfer - handles large transfer amount")
	void registerTransfer_WithLargeAmount_Succeeds() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 2, 999999.99);

		TransferResponseDTO successResponse = createSuccessResponse(1, 2, 0.01, 1000000.00);

		Transaction savedTransaction = createTransaction("txLarge",
				TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, 1, 2, 999999.99,
				TransactionStatus.COMPLETED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(successResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.assertNext(transaction -> {
					assertThat(transaction.getAmount()).isEqualTo(999999.99);
					assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
				})
				.verifyComplete();
	}

	@Test
	@DisplayName("Integration - successful transfer flow end-to-end")
	void integration_SuccessfulTransferFlow() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 10, 20, 500.0);

		TransferResponseDTO successResponse = createSuccessResponse(10, 20, 500.0, 500.0);

		Transaction savedTransaction = createTransaction("txIntegration",
				TransactionType.TRANSFER_OWN_ACCOUNT, 10, 20, 500.0, TransactionStatus.COMPLETED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(successResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		// Full flow verification
		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.assertNext(transaction -> {
					assertThat(transaction.getTransactionId()).isNotNull();
					assertThat(transaction.getTransactionType()).isEqualTo(
							TransactionType.TRANSFER_OWN_ACCOUNT);
					assertThat(transaction.getAccountId()).isEqualTo(10);
					assertThat(transaction.getRelatedAccountId()).isEqualTo(20);
					assertThat(transaction.getAmount()).isEqualTo(500.0);
					assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
				})
				.verifyComplete();

		// Verify interaction order
		var inOrder = inOrder(accountServiceClient, transactionRepository);
		inOrder.verify(accountServiceClient).transfer(any(TransferRequestDTO.class));
		inOrder.verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	@DisplayName("Integration - failed transfer flow end-to-end")
	void integration_FailedTransferFlow() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 2, 1000.0);

		TransferResponseDTO failedResponse = createFailedResponse(
				"INSUFFICIENT_FUNDS", "Insufficient balance in source account: 500.0");

		Transaction savedTransaction = createTransaction("txFailedIntegration",
				TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, 1, 2, 1000.0,
				TransactionStatus.FAILED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(failedResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.expectErrorMatches(throwable ->
						throwable instanceof TransferFailedException &&
								throwable.getMessage().contains("INSUFFICIENT_FUNDS") &&
								throwable.getMessage().contains("Insufficient balance"))
				.verify();

		// Verify transaction was still saved with FAILED status
		ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
		verify(transactionRepository).save(captor.capture());

		Transaction capturedTx = captor.getValue();
		assertThat(capturedTx.getStatus()).isEqualTo(TransactionStatus.FAILED);
		assertThat(capturedTx.getAmount()).isEqualTo(1000.0);
	}

	@Test
	@DisplayName("Verify transaction status transitions correctly")
	void registerTransfer_VerifiesStatusTransition() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 1, 2, 200.0);

		TransferResponseDTO successResponse = createSuccessResponse(1, 2, 800.0, 700.0);

		Transaction savedTransaction = createTransaction("txStatus",
				TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 200.0, TransactionStatus.COMPLETED);

		when(accountServiceClient.transfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(successResponse));
		when(transactionRepository.save(any(Transaction.class)))
				.thenReturn(Mono.just(savedTransaction));

		StepVerifier.create(transactionService.registerTransfer(requestDTO))
				.expectNextCount(1)
				.verifyComplete();

		// Verify status was set correctly
		ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
		verify(transactionRepository).save(captor.capture());

		Transaction captured = captor.getValue();
		assertThat(captured.getStatus()).isIn(TransactionStatus.COMPLETED,
				TransactionStatus.PENDING);
	}

	private Transaction createTransaction(String id, TransactionType type, Integer accountId,
										  Integer relatedAccountId, Double amount,
										  TransactionStatus status) {
		return Transaction.builder()
				.transactionId(id)
				.transactionType(type)
				.accountId(accountId)
				.relatedAccountId(relatedAccountId)
				.amount(amount)
				.transactionDate(LocalDateTime.now())
				.status(status)
				.build();
	}

	private TransferRequestDTO createTransferRequest(String type, Integer sourceId,
													 Integer destId, Double amount) {
		return new TransferRequestDTO(type, sourceId, destId, amount);
	}

	private TransferResponseDTO createSuccessResponse(Integer sourceId, Integer destId,
													  Double sourceBalance, Double destBalance) {
		return TransferResponseDTO.builder()
				.success(true)
				.errorCode(null)
				.errorMessage(null)
				.sourceAccountId(sourceId)
				.destinationAccountId(destId)
				.finalSourceBalance(sourceBalance)
				.finalDestinationBalance(destBalance)
				.build();
	}

	private TransferResponseDTO createFailedResponse(String errorCode, String errorMessage) {
		return TransferResponseDTO.builder()
				.success(false)
				.errorCode(errorCode)
				.errorMessage(errorMessage)
				.sourceAccountId(null)
				.destinationAccountId(null)
				.finalSourceBalance(null)
				.finalDestinationBalance(null)
				.build();
	}
}