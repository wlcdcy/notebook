package com.example.error;

public class AuthorizationError extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public AuthorizationError() {
        super();
    }

    public AuthorizationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AuthorizationError(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationError(String message) {
        super(message);
    }

    public AuthorizationError(Throwable cause) {
        super(cause);
    }

}
