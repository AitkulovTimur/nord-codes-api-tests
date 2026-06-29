package com.nordcodes.api.exception;

/**
 * Exception thrown when the NordCodes application fails to start.
 * <p>
 * This exception is used to indicate errors that occur during the startup
 * process of the NordCodes application, such as port conflicts, missing dependencies,
 * or configuration issues.
 * </p>
 */
public class NordCodesAppStartException extends RuntimeException {
    public NordCodesAppStartException(Exception e) {
        super("Failed to start tested application", e);
    }

    public NordCodesAppStartException(String message, Exception cause) {
        super(message, cause);
    }

}
