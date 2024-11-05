/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.exception;

import org.springframework.boot.ExitCodeGenerator;


/**
 * Custom unchecked exceptions superclass
 * Use to ensure no bare RuntimeException is thrown
 * whenever custom excpetion are caught and logged
 */
public class ApimArgsException extends RuntimeException implements ExitCodeGenerator {

    public ApimArgsException(String message) {
        super(message);
    }

    @Override
    public int getExitCode() {
        return 1;
    }
}
