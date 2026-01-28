package com.alessandragodoy.transactionms.model;

/**
 * Enum representing the types of transactions in the banking system.
 */
public enum TransactionType {
	TRANSFER_OWN_ACCOUNT,      // Transfer between customer accounts in the same bank
	TRANSFER_THIRD_PARTY_ACCOUNT     // Transfer to a third party account in the same bank
}
