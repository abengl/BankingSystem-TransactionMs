package com.alessandragodoy.transactionms.controller;

import com.alessandragodoy.transactionms.dto.TransactionDTO;
import com.alessandragodoy.transactionms.dto.TransferRequestDTO;
import com.alessandragodoy.transactionms.exception.TransactionNotFoundException;
import com.alessandragodoy.transactionms.model.Transaction;
import com.alessandragodoy.transactionms.model.TransactionStatus;
import com.alessandragodoy.transactionms.model.TransactionType;
import com.alessandragodoy.transactionms.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TransactionController.
 * Tests reactive REST endpoints for transaction management operations.
 */
@WebFluxTest(TransactionController.class)
class TransactionControllerTest {

	@Autowired
	private WebTestClient webTestClient;

	@MockitoBean
	private TransactionService transactionService;

	@Test
	@DisplayName("GET /api/v1/transactions - returns list of all transactions")
	void getAllTransactions_WithTransactions_ReturnsTransactionList() {

		Transaction transaction1 = createTransaction(
				"tx1", TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 100.0,
				TransactionStatus.COMPLETED);
		Transaction transaction2 = createTransaction(
				"tx2", TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, 2, 3, 200.0,
				TransactionStatus.COMPLETED);
		Transaction transaction3 = createTransaction(
				"tx3", TransactionType.TRANSFER_OWN_ACCOUNT, 1, 3, 50.0,
				TransactionStatus.COMPLETED);

		when(transactionService.getAllTransactions())
				.thenReturn(Flux.just(transaction1, transaction2, transaction3));

		webTestClient.get()
				.uri("/api/v1/transactions")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(TransactionDTO.class)
				.hasSize(3)
				.consumeWith(response -> {
					var transactions = response.getResponseBody();
					assert transactions.get(0).getTransactionId().equals("tx1");
					assert transactions.get(0).getAmount().equals(100.0);
					assert transactions.get(1).getTransactionId().equals("tx2");
					assert transactions.get(2).getAmount().equals(50.0);
				});

		verify(transactionService).getAllTransactions();
	}

	@Test
	@DisplayName("GET /api/v1/transactions - returns empty list when no transactions")
	void getAllTransactions_WithNoTransactions_ReturnsEmptyList() {

		when(transactionService.getAllTransactions()).thenReturn(Flux.empty());

		webTestClient.get()
				.uri("/api/v1/transactions")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(TransactionDTO.class)
				.hasSize(0);

		verify(transactionService).getAllTransactions();
	}

	@Test
	@DisplayName("GET /api/v1/transactions/{transactionId} - returns transaction when found")
	void getTransactionById_WithValidId_ReturnsTransaction() {

		String transactionId = "507f1f77bcf86cd799439011";
		Transaction transaction = createTransaction(
				transactionId, TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 150.0,
				TransactionStatus.COMPLETED);

		when(transactionService.getTransactionById(transactionId))
				.thenReturn(Mono.just(transaction));

		webTestClient.get()
				.uri("/api/v1/transactions/{transactionId}", transactionId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(TransactionDTO.class)
				.consumeWith(response -> {
					TransactionDTO dto = response.getResponseBody();
					assert dto.getTransactionId().equals(transactionId);
					assert dto.getTransactionType().equals(TransactionType.TRANSFER_OWN_ACCOUNT);
					assert dto.getAccountId().equals(1);
					assert dto.getRelatedAccountId().equals(2);
					assert dto.getAmount().equals(150.0);
					assert dto.getTransactionStatus().equals(TransactionStatus.COMPLETED);
				});

		verify(transactionService).getTransactionById(transactionId);
	}

	@Test
	@DisplayName("GET /api/v1/transactions/{transactionId} - returns 404 when transaction not " +
			"found")
	void getTransactionById_WithNonExistentId_ReturnsNotFound() {

		String nonExistentId = "nonexistent123";

		when(transactionService.getTransactionById(nonExistentId))
				.thenReturn(Mono.error(new TransactionNotFoundException(
						"Transaction not found with ID: " + nonExistentId)));

		webTestClient.get()
				.uri("/api/v1/transactions/{transactionId}", nonExistentId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound();

		verify(transactionService).getTransactionById(nonExistentId);
	}

	@Test
	@DisplayName("GET /api/v1/transactions/account/{accountId} - returns transactions for account")
	void getTransactionsByAccountId_WithTransactions_ReturnsTransactionList() {

		Integer accountId = 1;
		Transaction transaction1 = createTransaction(
				"tx1", TransactionType.TRANSFER_OWN_ACCOUNT, accountId, 2, 100.0,
				TransactionStatus.COMPLETED);
		Transaction transaction2 = createTransaction(
				"tx2", TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, accountId, 3, 200.0,
				TransactionStatus.FAILED);

		when(transactionService.getTransactionsByAccountId(accountId))
				.thenReturn(Flux.just(transaction1, transaction2));

		webTestClient.get()
				.uri("/api/v1/transactions/account/{accountId}", accountId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(TransactionDTO.class)
				.hasSize(2)
				.consumeWith(response -> {
					var transactions = response.getResponseBody();
					assert transactions.stream().allMatch(t -> t.getAccountId().equals(accountId));
					assert transactions.get(0).getAmount().equals(100.0);
					assert transactions.get(1).getAmount().equals(200.0);
				});

		verify(transactionService).getTransactionsByAccountId(accountId);
	}

	@Test
	@DisplayName("GET /api/v1/transactions/account/{accountId} - returns not found exception when " +
			"no transactions")
	void getTransactionsByAccountId_WithNoTransactions_ReturnsNotFound() {

		Integer accountId = 999;
		when(transactionService.getTransactionsByAccountId(accountId))
				.thenReturn(Flux.error(new TransactionNotFoundException(
						"No transactions found for account id: " + accountId)));

		webTestClient.get()
				.uri("/api/v1/transactions/account/{accountId}", accountId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound();

		verify(transactionService).getTransactionsByAccountId(accountId);
	}

	@Test
	@DisplayName("GET /api/v1/transactions/account/{accountId} - handles account with single " +
			"transaction")
	void getTransactionsByAccountId_WithSingleTransaction_ReturnsOneTransaction() {

		Integer accountId = 5;
		Transaction transaction = createTransaction(
				"tx1", TransactionType.TRANSFER_OWN_ACCOUNT, accountId, 6, 500.0,
				TransactionStatus.COMPLETED);

		when(transactionService.getTransactionsByAccountId(accountId))
				.thenReturn(Flux.just(transaction));

		webTestClient.get()
				.uri("/api/v1/transactions/account/{accountId}", accountId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(TransactionDTO.class)
				.hasSize(1);

		verify(transactionService).getTransactionsByAccountId(accountId);
	}

	@Test
	@DisplayName("POST /api/v1/transactions/transfer - registers own account transfer " +
			"successfully")
	void registerTransfer_WithOwnAccountTransfer_ReturnsCreatedTransaction() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 1, 2, 300.0);

		Transaction createdTransaction = createTransaction(
				"tx123", TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 300.0,
				TransactionStatus.COMPLETED);

		when(transactionService.registerTransfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(createdTransaction));

		webTestClient.post()
				.uri("/api/v1/transactions/transfer")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestDTO)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(TransactionDTO.class)
				.consumeWith(response -> {
					TransactionDTO dto = response.getResponseBody();
					assert dto.getTransactionId().equals("tx123");
					assert dto.getTransactionType().equals(TransactionType.TRANSFER_OWN_ACCOUNT);
					assert dto.getAccountId().equals(1);
					assert dto.getRelatedAccountId().equals(2);
					assert dto.getAmount().equals(300.0);
					assert dto.getTransactionStatus().equals(TransactionStatus.COMPLETED);
				});

		verify(transactionService).registerTransfer(any(TransferRequestDTO.class));
	}

	@Test
	@DisplayName("POST /api/v1/transactions/transfer - registers third party transfer " +
			"successfully")
	void registerTransfer_WithThirdPartyTransfer_ReturnsCreatedTransaction() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 1, 3, 500.0);

		Transaction createdTransaction = createTransaction(
				"tx456", TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, 1, 3, 500.0,
				TransactionStatus.COMPLETED);

		when(transactionService.registerTransfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(createdTransaction));

		webTestClient.post()
				.uri("/api/v1/transactions/transfer")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestDTO)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(TransactionDTO.class)
				.consumeWith(response -> {
					TransactionDTO dto = response.getResponseBody();
					assert dto.getTransactionType()
							.equals(TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT);
					assert dto.getAmount().equals(500.0);
				});

		verify(transactionService).registerTransfer(any(TransferRequestDTO.class));
	}

	@ParameterizedTest
	@MethodSource("invalidTransferRequests")
	@DisplayName("POST /api/v1/transactions/transfer - returns 400 for invalid input")
	void registerTransfer_WithInvalidData_ReturnsBadRequest(TransferRequestDTO invalidRequest) {

		webTestClient.post()
				.uri("/api/v1/transactions/transfer")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(invalidRequest)
				.exchange()
				.expectStatus().isBadRequest();

		verifyNoInteractions(transactionService);
	}

	static Stream<TransferRequestDTO> invalidTransferRequests() {
		return Stream.of(
				// Null transaction type
				new TransferRequestDTO(null, 1, 2, 100.0),
				// Invalid transaction type
				new TransferRequestDTO("INVALID_TYPE", 1, 2, 100.0),
				//Empty transaction type
				new TransferRequestDTO("", 1, 2, 100.0),
				// Null source account
				new TransferRequestDTO("TRANSFER_OWN_ACCOUNT", null, 2, 100.0),
				// Null destination account
				new TransferRequestDTO("TRANSFER_OWN_ACCOUNT", 1, null, 100.0),
				// Null amount
				new TransferRequestDTO("TRANSFER_OWN_ACCOUNT", 1, 2, null),
				// Zero amount
				new TransferRequestDTO("TRANSFER_OWN_ACCOUNT", 1, 2, 0.0),
				// Negative amount
				new TransferRequestDTO("TRANSFER_THIRD_PARTY_ACCOUNT", 1, 3, -100.0),
				// Zero source account
				new TransferRequestDTO("TRANSFER_THIRD_PARTY_ACCOUNT", 0, 3, 100.0),
				// Negative source account
				new TransferRequestDTO("TRANSFER_THIRD_PARTY_ACCOUNT", -1, 2, 100.0),
				// Zero destination account
				new TransferRequestDTO("TRANSFER_THIRD_PARTY_ACCOUNT", 1, 0, 100.0),
				// Negative destination account
				new TransferRequestDTO("TRANSFER_THIRD_PARTY_ACCOUNT", 1, -2, 100.0)
		);
	}

	@Test
	@DisplayName("GET /api/v1/transactions - handles large number of transactions")
	void getAllTransactions_WithManyTransactions_ReturnsAllTransactions() {

		Flux<Transaction> manyTransactions = Flux.range(1, 100)
				.map(i -> createTransaction(
						"tx" + i,
						i % 2 == 0 ? TransactionType.TRANSFER_OWN_ACCOUNT :
								TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT,
						i,
						i + 1,
						100.0 * i,
						TransactionStatus.COMPLETED
				));

		when(transactionService.getAllTransactions()).thenReturn(manyTransactions);

		webTestClient.get()
				.uri("/api/v1/transactions")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(TransactionDTO.class)
				.hasSize(100);

		verify(transactionService).getAllTransactions();
	}

	@Test
	@DisplayName("POST /api/v1/transactions/transfer - registers transfer with small amount")
	void registerTransfer_WithSmallAmount_ReturnsCreatedTransaction() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_OWN_ACCOUNT", 1, 2, 0.01);

		Transaction createdTransaction = createTransaction(
				"tx789", TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 0.01,
				TransactionStatus.COMPLETED);

		when(transactionService.registerTransfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(createdTransaction));

		webTestClient.post()
				.uri("/api/v1/transactions/transfer")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestDTO)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(TransactionDTO.class)
				.consumeWith(response -> {
					TransactionDTO dto = response.getResponseBody();
					assert dto.getAmount().equals(0.01);
				});

		verify(transactionService).registerTransfer(any(TransferRequestDTO.class));
	}

	@Test
	@DisplayName("POST /api/v1/transactions/transfer - registers transfer with large amount")
	void registerTransfer_WithLargeAmount_ReturnsCreatedTransaction() {

		TransferRequestDTO requestDTO = createTransferRequest(
				"TRANSFER_THIRD_PARTY_ACCOUNT", 3, 2, 999999.99);

		Transaction createdTransaction = createTransaction(
				"tx999", TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, 1, 2, 999999.99,
				TransactionStatus.COMPLETED);

		when(transactionService.registerTransfer(any(TransferRequestDTO.class)))
				.thenReturn(Mono.just(createdTransaction));

		webTestClient.post()
				.uri("/api/v1/transactions/transfer")
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(requestDTO)
				.exchange()
				.expectStatus().isCreated()
				.expectBody(TransactionDTO.class)
				.consumeWith(response -> {
					TransactionDTO dto = response.getResponseBody();
					assert dto.getAmount().equals(999999.99);
				});

		verify(transactionService).registerTransfer(any(TransferRequestDTO.class));
	}

	@Test
	@DisplayName("GET /api/v1/transactions/account/{accountId} - handles transactions for " +
			"different transaction types")
	void getTransactionsByAccountId_WithMixedTypes_ReturnsAllTypes() {

		Integer accountId = 10;
		Transaction ownAccountTransfer = createTransaction(
				"tx1", TransactionType.TRANSFER_OWN_ACCOUNT, accountId, 11, 100.0,
				TransactionStatus.COMPLETED);
		Transaction thirdPartyTransfer = createTransaction(
				"tx2", TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT, accountId, 12, 200.0,
				TransactionStatus.COMPLETED);

		when(transactionService.getTransactionsByAccountId(accountId))
				.thenReturn(Flux.just(ownAccountTransfer, thirdPartyTransfer));

		webTestClient.get()
				.uri("/api/v1/transactions/account/{accountId}", accountId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(TransactionDTO.class)
				.hasSize(2)
				.consumeWith(response -> {
					var transactions = response.getResponseBody();
					assert transactions.stream()
							.anyMatch(t -> t.getTransactionType()
									.equals(TransactionType.TRANSFER_OWN_ACCOUNT));
					assert transactions.stream()
							.anyMatch(t -> t.getTransactionType()
									.equals(TransactionType.TRANSFER_THIRD_PARTY_ACCOUNT));
				});

		verify(transactionService).getTransactionsByAccountId(accountId);
	}

	@Test
	@DisplayName("GET /api/v1/transactions/{transactionId} - handles ObjectId format")
	void getTransactionById_WithValidObjectIdFormat_ReturnsTransaction() {

		String mongoId = "507f1f77bcf86cd799439011";
		Transaction transaction = createTransaction(
				mongoId, TransactionType.TRANSFER_OWN_ACCOUNT, 1, 2, 250.0,
				TransactionStatus.COMPLETED);

		when(transactionService.getTransactionById(mongoId))
				.thenReturn(Mono.just(transaction));

		webTestClient.get()
				.uri("/api/v1/transactions/{transactionId}", mongoId)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(TransactionDTO.class)
				.consumeWith(response -> {
					TransactionDTO dto = response.getResponseBody();
					assert dto.getTransactionId().equals(mongoId);
				});

		verify(transactionService).getTransactionById(mongoId);
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
}