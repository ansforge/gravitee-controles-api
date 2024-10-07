/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.exception;

import org.springframework.boot.ExitCodeGenerator;


public class ApimFileNotFoundException extends RuntimeException implements ExitCodeGenerator {

    /**
     * Constructs a {@code ApimFileNotFoundException} with the
     * specified detail message and a dedicated exit code.
     * The string {@code message} can be retrieved later by the
     * {@link Throwable#getMessage}
     * method of class {@code java.lang.Throwable}.
     *
     * @param message the detail message.
     */
    public ApimFileNotFoundException(String message) {
        super(message);
    }

    /**
     * Returns the exit code that should be returned from the application.
     *
     * @return the exit code.
     */
    @Override
    public int getExitCode() {
        return 3;
    }
}
