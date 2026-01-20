package com.alessandragodoy.transactionms.model;

/**
 * Enum representing the types of transactions in the banking system.
 */
public enum TransactionType {
	TRANSFER_OWN_ACCOUNT,      // Transfer between customer accounts in the same bank
	TRANSFER_INTER_ACCOUNT     // Transfer from the customer account to a different bank
}
