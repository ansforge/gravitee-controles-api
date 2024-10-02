/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ExitCodeGenerator;


/**
 * Custom unchecked exception in charge of immediately stopping
 * the application if not all the required arguments are given
 * in the starting command line.
 */
@Slf4j
public class ApimMissingArgumentException extends RuntimeException implements ExitCodeGenerator {

    /**
     * Constructs a {@code ApimMissingArgumentException} with the
     * specified detail message and a help message listing the
     * required command line arguments, with a dedicated exit code.
     * The string {@code message} can be retrieved later by the
     * {@link Throwable#getMessage}
     * method of class {@code java.lang.Throwable}.
     *
     * @param message the detail message.
     */
    public ApimMissingArgumentException(String message) {
        super(message);
        logRequiredArgs();
    }

    /**
     * Returns the exit code that should be returned from the application.
     *
     * @return the exit code.
     */
    @Override
    public int getExitCode() {
        return 2;
    }

    /**
     * Logs the command line arguments required to start the application.
     */
    private void logRequiredArgs() {
        final String msg = """
                Required arguments:
                --env={{apim_environment_id}}
                --apikey={{apim_api_key}}
                --recipients.filepath={{recipients_list_filepath}}""";
        log.error(msg);
    }
}
