/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.notification;

import fr.gouv.esante.apim.checkrules.exception.ApimRulecheckerException;
import lombok.Getter;

import java.time.Instant;


@Getter
public class ErrorEmail {

    private String from;
    private String subject;
    private String body;

    public ErrorEmail(ApimRulecheckerException exception, String envId) {
        this.from = "noreply@esante.gouv.fr";
        this.subject = writeEmailSubject(envId);
        this.body = writeEmailBody(exception);
    }

    private String writeEmailSubject(String env) {
        return String.format("[APIM] Erreur lors du contrôle des règles d'implémentation sur l'environnement %s", env);
    }

    private String writeEmailBody(ApimRulecheckerException exception) {
        return "Détails de l'erreur :\n" + String.format("Date de l'exécution : %s\n", Instant.now()) +
                exception.getMessage();
    }
}
