package com.alessandragodoy.transactionms.adapter;

import com.alessandragodoy.transactionms.dto.TransferRequestDTO;
import com.alessandragodoy.transactionms.dto.TransferResponseDTO;
import com.alessandragodoy.transactionms.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

/**
 * Service client for handling transactions.
 */
@Component
@RequiredArgsConstructor
public class AccountServiceClient {

	private final WebClient webClient;

	public Mono<TransferResponseDTO> transfer(TransferRequestDTO transferRequestDTO) {

		return webClient.patch().uri("/execute-transfer")
				.bodyValue(transferRequestDTO)
				.retrieve()
				.bodyToMono(TransferResponseDTO.class)
				.onErrorMap(
						WebClientRequestException.class, error -> new ExternalServiceException(
								"There is an error on the account service: " + error.getMessage()));
	}

}
