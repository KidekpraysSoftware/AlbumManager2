package com.kidekdev.albummanager.database.service;

/**
 * Represents outcome of filesystem database operations.
 *
 * @param isSuccess indicates whether operation succeeded
 * @param message human-readable message about result
 */
public record OperationResult(boolean isSuccess, String message) {

    public OperationResult {
        if (message == null || message.isBlank()) {
            message = "";
        }
    }
}
