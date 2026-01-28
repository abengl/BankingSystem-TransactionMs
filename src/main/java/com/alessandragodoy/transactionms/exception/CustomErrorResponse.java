package com.alessandragodoy.transactionms.exception;

import java.time.LocalDateTime;

/**
 * A record representing a custom error response.
 *
 * @param timestamp the timestamp of the error
 * @param message   the error message
 * @param path      the request path that caused the error
 */
public record CustomErrorResponse(LocalDateTime timestamp, String message, String path) {
}
