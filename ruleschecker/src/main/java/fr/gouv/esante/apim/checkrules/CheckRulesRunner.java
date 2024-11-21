/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules;

import fr.gouv.esante.apim.checkrules.exception.ApimRulecheckerException;
import fr.gouv.esante.apim.checkrules.model.notification.Notification;
import fr.gouv.esante.apim.checkrules.model.results.Report;
import fr.gouv.esante.apim.checkrules.services.notification.EmailNotifier;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ArgumentsChecker;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.CheckRulesService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;


/**
 * Point d'entrée de l'application
 */
@Component
@Getter
@Slf4j
public class CheckRulesRunner implements ApplicationRunner {

    private final ArgumentsChecker argsParser;
    private final CheckRulesService checkRulesService;
    private final EmailNotifier emailNotifier;

    @Value("${envid}")
    private String envId;

    private Report report;

    private Notification notification;


    public CheckRulesRunner(ArgumentsChecker argsParser,
                            CheckRulesService checkRulesService,
                            EmailNotifier emailNotifier) {
        this.argsParser = argsParser;
        this.checkRulesService = checkRulesService;
        this.emailNotifier = emailNotifier;
    }

    /**
     * Méthode principale de l'application :
     * - Vérifie les arguments donnés en ligne de commande
     * - Vérifie les règles sur chaque API
     * - Envoie le rapport des vérifications par notifications
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        log.debug("Start checking rules with args : {}", Arrays.toString(args.getSourceArgs()));
        // Récupération et validation des arguments d'entrée
        argsParser.verifyArgs(args);
        // Lancement des vérifications des règles et génération du rapport
        try {
            report = checkRulesService.check();
            // Préparation et envoi des notifications par mail
            notification = emailNotifier.notify(report);
        } catch (ApimRulecheckerException e) {
            // Envoi de l'email d'erreur aux destinataires
            notification = emailNotifier.notifyError(e, envId);
        }
    }




}
