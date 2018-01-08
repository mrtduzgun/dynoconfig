package com.trendyol.dynoconfig;

/**
 * @author Murat Duzgun
 * <p>
 * Exception thrown if Dynoconfig encounters a problem
 */
public class DynoconfigException extends RuntimeException {

    public DynoconfigException() {
    }

    public DynoconfigException(String message) {
        super(message);
    }

    public DynoconfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
