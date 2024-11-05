/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.exception;

public class ApimRulecheckerException extends Exception {

    public ApimRulecheckerException(String message) {
        super(message);
    }

    public ApimRulecheckerException(String message, Throwable cause) {
        super(message, cause);
    }
}
