/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.notification;

import fr.gouv.esante.apim.checkrules.exception.ApimRulecheckerException;
import lombok.Getter;

import java.time.Instant;


@Getter
public class ErrorEmail {

    private final String from;
    private final String subject;
    private final String body;

    public ErrorEmail(ApimRulecheckerException exception, String from, String envId) {
        this.from = from;
        this.subject = writeEmailSubject(envId);
        this.body = writeEmailBody(exception);
    }

    private String writeEmailSubject(String env) {
        return String.format("[APIM] Erreur lors du contrôle des règles d'implémentation sur l'environnement %s", env);
    }

    private String writeEmailBody(ApimRulecheckerException exception) {
        return String.format("Détails de l'erreur :%sDate de l'exécution : %s%s",
                System.lineSeparator(),
                Instant.now(),
                exception.getMessage()
        );
    }
}
