/*
 * (c) Copyright 2024-2024, ANS. All rights reserved.
 */
package fr.gouv.esante.apim.checkrules;

import fr.gouv.esante.apim.checkrules.exception.ApimRulecheckerException;
import fr.gouv.esante.apim.checkrules.model.results.ApiDefinitionCheckResult;
import fr.gouv.esante.apim.checkrules.model.definition.GraviteeApiDefinition;
import fr.gouv.esante.apim.checkrules.model.results.Report;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ApiDefinitionLoader;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.ArgumentsChecker;
import fr.gouv.esante.apim.checkrules.services.rulesvalidation.RulesChecker;

import fr.gouv.esante.apim.checkrules.services.notification.EmailNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Point d'entrée de l'application
 */
@Component
@Slf4j
public class CheckRulesRunner implements ApplicationRunner {

    private final ArgumentsChecker argsParser;
    private final ApiDefinitionLoader loader;
    private final RulesChecker rulesChecker;
    private final EmailNotifier emailNotifier;

    @Value("${envid}")
    private String envId;


    public CheckRulesRunner(ArgumentsChecker argsParser,
                            ApiDefinitionLoader loader,
                            RulesChecker rulesChecker, EmailNotifier emailNotifier) {
        this.argsParser = argsParser;
        this.loader = loader;
        this.rulesChecker = rulesChecker;
        this.emailNotifier = emailNotifier;
    }

    /**
     * Méthode principale de l'application :
     *  - Vérifie les arguments donnés en ligne de commande
     *  - Vérifie les règles sur chaque API
     *  - Envoie le rapport des vérifications par notifications
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("Start checking rules with args : {}", Arrays.toString(args.getSourceArgs()));
        // Récupération et validation des arguments d'entrée
        argsParser.verifyArgs(args);
        // Lancement des vérifications des règles et génération du rapport
        Report report;
        try {
            // Chargement de toutes les définitions d'API de l'APIM
            List<GraviteeApiDefinition> apis = loader.loadApiDefinitions();
            // Controle de la conformité des APIs
            Map<String, ApiDefinitionCheckResult> results = checkRulesForEachApi(apis);
            // Génération du rapport de controle
            report = new Report(results, Instant.now().toString(), envId);
            log.info("Finished checking rules, preparing to send notifications : {}", report);
            // Préparation et envoi des notifications par mail
            emailNotifier.notify(report);
        } catch (ApimRulecheckerException e) {
            // Envoi de l'email d'erreur aux destinataires
            emailNotifier.notifyError(e, envId);
        }
    }

    /**
     * Vérification des règles d'implémentation d'une API dans l'APIM.
     *
     * @param apis List of API definitions to check
     * @return checkresults
     */
    private Map<String, ApiDefinitionCheckResult> checkRulesForEachApi(List<GraviteeApiDefinition> apis) {
        Map<String, ApiDefinitionCheckResult> checkResults = new HashMap<>();
        for (GraviteeApiDefinition apiDefinition : apis) {
            checkResults.put(
                    apiDefinition.getApiName(),
                    rulesChecker.checkRules(apiDefinition)
            );
        }
        return checkResults;
    }
}
