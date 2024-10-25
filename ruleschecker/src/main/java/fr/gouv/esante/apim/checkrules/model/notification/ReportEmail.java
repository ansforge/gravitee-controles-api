/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules.model.notification;

import fr.gouv.esante.apim.checkrules.model.results.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.results.Report;
import fr.gouv.esante.apim.checkrules.model.results.RuleResult;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Map;


@Getter
@Setter
@Slf4j
public class ReportEmail {

    private String from;
    private String subject;
    private String body;
    private File attachment;


    public ReportEmail(Report report, String from) {
        this.from = from;
        this.subject = writeEmailSubject(report);
        this.body = writeEmailBody(report);
        this.attachment = buildAttachment(report);
    }

    private String writeEmailSubject(Report report) {
        return String.format("[APIM] %s du contrôle des règles d'implémentation sur l'environnement %s",
                report.isSuccess() ? "Succès" : "Echec",
                report.getEnvironment());
    }

    private String writeEmailBody(Report report) {
        String body = "Synthèse des résultats :";
        StringBuilder sb = new StringBuilder(body);
        sb.append(System.lineSeparator());
        if (report.isSuccess()) {
            sb.append("Toutes les APIs contrôlées sont conformes aux règles d'implémentation dans l'APIM")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        } else {
            sb.append("Au moins une infraction aux règles d'implémentation dans l'APIM a été détectée")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
        for (Map.Entry<String, ApiDefinitionCheckResult> entry : report.getGlobalCheckResults().entrySet()) {
            String apiName = entry.getKey();
            ApiDefinitionCheckResult apiCheckResult = entry.getValue();
            sb.append(apiName)
                    .append(" : ");
            String apiResult = "Succès - Respecte toutes les règles d'implementation";
            for (RuleResult ruleResult : apiCheckResult.getRuleResults()) {
                if (!ruleResult.isSuccess()) {
                    apiResult = "Echec - Non conforme aux règles d'implementation";
                    break;
                }
            }
            sb.append(apiResult)
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }

    private File buildAttachment(Report report) {
        // Write report in temp file
        File tempFile = null;
        try {
            tempFile = Files.createTempFile("report", "json").toFile();

            try (PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {
                writer.print(report.toString());
            } catch (IOException ex) {
                log.error("Erreur lors de l'écriture du fichier de résultats", ex);
            }
        } catch (IOException e) {
            log.error("Erreur lors de la création du fichier de résultats");
        }

        return tempFile;
    }
}
